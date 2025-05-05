import { NextRequest, NextResponse } from 'next/server'
import fs from 'fs'
import path from 'path'

// Define a type for user records (can be shared with other route)
interface UserRecord {
  id: number;
  name: string;
  email: string;
  role: string;
  password?: string;
  createdAt: string;
}

const DB = path.join(process.cwd(), 'users.json')
const read = (): UserRecord[] => JSON.parse(fs.readFileSync(DB,'utf8'))
const write = (d: any) => fs.writeFileSync(DB, JSON.stringify(d,null,2))

export async function GET(_req: NextRequest, { params }: { params: { id: string } }) {
  const user = read().find(u => u.id === +params.id)
  return user? NextResponse.json(user) : NextResponse.json({},{ status:404 })
}
export async function PUT(req: NextRequest, { params }: { params: { id: string } }) {
  const upd = await req.json()
  const all = read()
  const idx = all.findIndex(u => u.id === +params.id)
  if(idx<0) return NextResponse.json({},{ status:404 })
  all[idx] = { ...all[idx], ...upd }
  write(all)
  return NextResponse.json(all[idx])
}
export async function DELETE(_req: NextRequest, { params }: { params: { id: string } }) {
  // Delete the user and return the deleted id with 200 status
  const userId = parseInt(params.id, 10)
  const all = read()
  const filtered = all.filter(u => u.id !== userId)
  write(filtered)
  return NextResponse.json({ id: userId }, { status: 200 })
}
