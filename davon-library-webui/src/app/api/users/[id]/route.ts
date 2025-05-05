import { NextRequest, NextResponse } from 'next/server'
import fs from 'fs'
import path from 'path'

const DB = path.join(process.cwd(), 'users.json')
const read = () => JSON.parse(fs.readFileSync(DB,'utf8'))
const write = (d: any) => fs.writeFileSync(DB, JSON.stringify(d,null,2))

export async function GET(_req:NextRequest,{ params:{ id }}) {
  const user = read().find(u=>u.id===+id)
  return user? NextResponse.json(user) : NextResponse.json({},{ status:404 })
}
export async function PUT(req:NextRequest,{ params:{ id }}) {
  const upd = await req.json()
  const all = read()
  const idx = all.findIndex(u=>u.id===+id)
  if(idx<0) return NextResponse.json({},{ status:404 })
  all[idx] = { ...all[idx], ...upd }
  write(all)
  return NextResponse.json(all[idx])
}
export async function DELETE(_req:NextRequest,{ params:{ id }}) {
  let all = read()
  all = all.filter(u=>u.id!==+id)
  write(all)
  return NextResponse.json({}, { status:204 })
}
