import { NextRequest, NextResponse } from "next/server";
import { sign } from "jsonwebtoken";
import { serialize } from "cookie";

const JWT_SECRET = process.env.JWT_SECRET || "your-secret-key";
const NODE_ENV = process.env.NODE_ENV || "development";

export async function POST(req: NextRequest) {
  const body = await req.json();
  const { username, password } = body;

  // In a real application, you would validate the username and password against a database.
  if (username === "admin" && password === "admin") {
    const user = {
      username: "admin",
      roles: ["admin"],
    };

    const token = sign(
      {
        ...user,
        exp: Math.floor(Date.now() / 1000) + 60 * 60 * 24 * 30, // 30 days
      },
      JWT_SECRET
    );

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
  } else {
    return new NextResponse(
      JSON.stringify({ status: "error", message: "Invalid credentials" }),
      { status: 401 }
    );
  }
}
