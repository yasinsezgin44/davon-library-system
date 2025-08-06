// frontend/types/reservation.ts
import { Book } from './book';

export interface Reservation {
  id: number;
  book: Book;
  reservationTime: string;
  status: string;
}
