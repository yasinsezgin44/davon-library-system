import { NextRequest, NextResponse } from "next/server";
import { serialize } from "cookie";
import { jwtVerify } from "jose";

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

    const { payload: user } = await jwtVerify(token, JWT_SECRET);

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
