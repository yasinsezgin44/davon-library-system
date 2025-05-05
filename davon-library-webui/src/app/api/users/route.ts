import { NextRequest, NextResponse } from 'next/server'
import fs from 'fs'
import path from 'path'

const DB = path.join(process.cwd(), 'users.json')

// Define a type for user records
interface UserRecord {
  id: number;
  name: string;
  email: string;
  role: string;
  password?: string;
  createdAt: string;
}

// Read/write helpers with typed return
const read = (): UserRecord[] => JSON.parse(fs.readFileSync(DB, 'utf8'))
const write = (d: any) => fs.writeFileSync(DB, JSON.stringify(d, null,2))

export async function GET(req: NextRequest) {
  const url = new URL(req.url)
  const all = read()
  const total = all.length
  // parse pagination params from query (react-admin uses _start and _end)
  const start = parseInt(url.searchParams.get('_start') || '0', 10)
  const endParam = parseInt(url.searchParams.get('_end') || String(total), 10)
  // slice records for this page
  const records = all.slice(start, endParam)
  // build Content-Range header: items <start>-<end>/<total>
  const contentRange = `users ${start}-${start + records.length - 1}/${total}`
  return NextResponse.json(records, {
    status: 200,
    headers: {
      'Content-Range': contentRange,
      'Access-Control-Expose-Headers': 'Content-Range',
    },
  })
}

export async function POST(req: NextRequest) {
  const newUser = await req.json() as Partial<UserRecord>
  const all = read()
  const nextId = all.length ? Math.max(...all.map(u => u.id)) + 1 : 1
  const record: UserRecord = { id: nextId, ...newUser as any, createdAt: new Date().toISOString() }
  all.push(record)
  write(all)
  return NextResponse.json(record, { status: 201 })
}
