import { NextRequest, NextResponse } from 'next/server'
import fs from 'fs'
import path from 'path'

const DB = path.join(process.cwd(), 'data/users.json')
const read = () => JSON.parse(fs.readFileSync(DB, 'utf8'))
const write = (d: any) => fs.writeFileSync(DB, JSON.stringify(d, null,2))

export async function GET() {
  const users = read()
  return NextResponse.json(users, { headers:{ 'X-Total-Count': users.length.toString() } })
}
export async function POST(req: NextRequest) {
  const newUser = await req.json()
  const all = read()
  const nextId = all.length ? Math.max(...all.map(u=>u.id))+1 : 1
  const record = { id: nextId, ...newUser, createdAt: new Date().toISOString() }
  all.push(record); write(all)
  return NextResponse.json(record,{ status:201 })
}
