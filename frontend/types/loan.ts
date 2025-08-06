// frontend/types/loan.ts
import { BookCopy } from './book-copy';

export interface Loan {
  id: number;
  bookCopy: BookCopy;
  checkoutDate: string;
  dueDate: string;
  returnDate?: string;
  status: string;
}
