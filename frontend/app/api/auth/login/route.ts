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
    const response = await fetch("http://127.0.0.1:8083/api/auth/login", {
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
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1U1SDocGLCYpg2jMeZ2Q
+ignO7yHZXGT1mff+YCSpdmtTSxLCj/uu8ATyT451meUV8PLxm7fjKSHpRsu8gBu
bjjdZwulNUfVqM5Ib7ao7oxF/1/FdxBjuMQt3C7i2SDkje3gm07CtbwS0fe5XPtQ
Cd/kA3Jv9GcxUD2hmflMIILn9jC8F1CHjN2ktfGKSMqRtlbsIdCda4b9yu0AWEqE
TY8Zwqkf5M4bcOUkI/D0jR6cw0t805QDlbMXYBMvMp5FTbgKY0X5yPOQzvVoya74
YH5R+uJBM5ip7kKDDUcmgD3nz0nHNFC8BDSzq8sIjtvjMbuv92M+nPrBP2iZC5pL
CQIDAQAB
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
