import { NextRequest, NextResponse } from "next/server";

const BACKEND_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8083/api";

export async function PUT(request: NextRequest, { params }: { params: { id: string } }) {
  const token = request.cookies.get("token")?.value;
  if (!token) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  const resp = await fetch(`${BACKEND_BASE}/loans/${encodeURIComponent(params.id)}/return`, {
    method: "PUT",
    headers: { Authorization: `Bearer ${token}` },
  });
  const body = await resp.text();
  return new NextResponse(body, { status: resp.status, headers: { "Content-Type": resp.headers.get("Content-Type") || "application/json" } });
}


