// frontend/components/MyLoans.tsx
"use client";

import { Loan } from "@/types/loan";
import { LibraryButton } from "./library-button";

interface MyLoansProps {
  readonly loans: Loan[];
  readonly onReturn: (id: number) => void;
}

export function MyLoans({ loans, onReturn }: MyLoansProps) {
  return (
    <table className="min-w-full bg-white">
      <thead>
        <tr>
          <th className="py-2">Book</th>
          <th className="py-2">Due Date</th>
          <th className="py-2">Actions</th>
        </tr>
      </thead>
      <tbody>
        {loans.map((loan) => (
          <tr key={loan.id}>
            <td className="border px-4 py-2">{loan.bookCopy.book.title}</td>
            <td className="border px-4 py-2">
              {new Date(loan.dueDate).toLocaleDateString()}
            </td>
            <td className="border px-4 py-2">
              <LibraryButton onClick={() => onReturn(loan.id)}>
                Return
              </LibraryButton>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
