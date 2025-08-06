// frontend/types/book-copy.ts
import { Book } from './book';

export interface BookCopy {
  id: number;
  book: Book;
  status: string;
}
