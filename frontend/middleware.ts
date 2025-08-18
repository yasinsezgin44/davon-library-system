import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { jwtVerify, importSPKI } from "jose";

export async function middleware(req: NextRequest) {
  const token = req.cookies.get("token")?.value;

  if (!token) {
    return NextResponse.redirect(new URL("/auth/login", req.url));
  }

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

  try {
    await jwtVerify(token, rsaPublicKey);
    return NextResponse.next();
  } catch (err) {
    return NextResponse.redirect(new URL("/auth/login", req.url));
  }
}

export const config = {
  matcher: ["/dashboard/:path*", "/profile"],
};
