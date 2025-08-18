import { NextRequest, NextResponse } from "next/server";
import { serialize } from "cookie";
import { jwtVerify, importSPKI } from "jose";

const JWT_SECRET = new TextEncoder().encode(
  process.env.JWT_SECRET || "your-secret-key"
);
const NODE_ENV = process.env.NODE_ENV || "development";

export async function POST(req: NextRequest) {
  const body = await req.json();
  const { username, password } = body;

  try {
    const response = await fetch("http://localhost:8083/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      const errorData = await response.json();
      return new NextResponse(
        JSON.stringify({
          status: "error",
          message: errorData.message || "Invalid credentials",
        }),
        { status: response.status }
      );
    }

    const { token } = await response.json();

    const publicKey = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA72eygT6HYBeHVfRFXBAH
AgFWGTLNuMWlCcdYdfse/izcLAj/aVu3C/5/cCae4HBBNK2MwaTyhZ+nCpkg2yhi
m5pZB5HVqYKlVARP2Rk0YcKLFJELIpUy7smrrpac1bbgJH/KFuWokigg7+jxzFgg
ubp1hVQbOPT6HgkKlbOAO6HFv5EBQ+BUuYgo2EpcodBgRmzZZi6u1lMWrxMgTP/C
GFj/Ys0V0F4UHFiv1wxjTc7QwfUfKRh6ZI5QLBn/bL5AoH0Mkf0eTymIRTz9wEU5
X0dfznxR35YMGhZNJwMdzUhjDjwYSH9M8kXJNT1EIeSvAS/7uGGHwvtb6XbOY7/A
3QIDAQAB
-----END PUBLIC KEY-----`;

    const rsaPublicKey = await importSPKI(publicKey, "RS256");

    const { payload: user } = await jwtVerify(token, rsaPublicKey);

    const serialized = serialize("token", token, {
      httpOnly: true,
      secure: NODE_ENV !== "development",
      sameSite: "strict",
      maxAge: 60 * 60 * 24 * 30,
      path: "/",
    });

    return new NextResponse(JSON.stringify({ status: "success", user }), {
      status: 200,
      headers: { "Set-Cookie": serialized },
    });
  } catch (error) {
    console.error("Login error:", error);
    return new NextResponse(
      JSON.stringify({
        status: "error",
        message: "An internal server error occurred",
      }),
      { status: 500 }
    );
  }
}
