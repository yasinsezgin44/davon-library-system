// frontend/components/forms/BookForm.tsx
"use client";

import { Book } from "@/types/book";
import { useForm, SubmitHandler } from "react-hook-form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { LibraryButton } from "@/components/library-button";

interface BookFormProps {
  readonly onSubmit: SubmitHandler<Book>;
  readonly defaultValues?: Book;
}

export function BookForm({ onSubmit, defaultValues }: BookFormProps) {
  const { register, handleSubmit } = useForm<Book>({ defaultValues });

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="space-y-4">
        <div>
          <Label htmlFor="title">Title</Label>
          <Input
            id="title"
            type="text"
            placeholder="Enter book title"
            data-testid="title-input"
            {...register("title", { required: true })}
          />
        </div>
        <div>
          <Label htmlFor="author">Author</Label>
          <Input
            id="author"
            type="text"
            placeholder="Enter author name"
            data-testid="author-input"
            {...register("author", { required: true })}
          />
        </div>
        <div>
          <Label htmlFor="isbn">ISBN</Label>
          <Input
            id="isbn"
            type="text"
            placeholder="Enter ISBN"
            data-testid="isbn-input"
            {...register("isbn", { required: true })}
          />
        </div>
        <div>
          <Label htmlFor="publishedDate">Published Date</Label>
          <Input
            id="publishedDate"
            type="date"
            data-testid="publishedDate-input"
            {...register("publishedDate", { required: true })}
          />
        </div>
      </div>
      <LibraryButton type="submit" className="mt-6">
        Submit
      </LibraryButton>
    </form>
  );
}
