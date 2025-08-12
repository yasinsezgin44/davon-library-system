"use client";

import { useState } from "react";

interface CreateBookModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreate: (
    bookData: Omit<Book, "id" | "quantity" | "authors"> & {
      authorIds: number[];
      publisherId: number;
      categoryId: number;
    }
  ) => void;
}

export type Author = {
  id: number;
  name: string;
};

export type Book = {
  id: number;
  title: string;
  authors: Author[];
  isbn: string;
  quantity: number;
};

const CreateBookModal = ({
  isOpen,
  onClose,
  onCreate,
}: CreateBookModalProps) => {
  const [title, setTitle] = useState("");
  const [isbn, setIsbn] = useState("");
  const [authorIds, setAuthorIds] = useState<number[]>([]);
  const [publisherId, setPublisherId] = useState<number | "">("");
  const [categoryId, setCategoryId] = useState<number | "">("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (publisherId === "" || categoryId === "") {
      // Basic validation
      alert("Please fill out all fields.");
      return;
    }
    onCreate({
      title,
      isbn,
      authorIds,
      publisherId: Number(publisherId),
      categoryId: Number(categoryId),
    });
    // Reset form
    setTitle("");
    setIsbn("");
    setAuthorIds([]);
    setPublisherId("");
    setCategoryId("");
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full text-black">
      <div className="relative top-20 mx-auto p-5 border w-full max-w-2xl shadow-lg rounded-md bg-white">
        <div className="mt-3 text-center">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Add New Book
          </h3>
          <form className="mt-4" onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="mb-4">
                <label
                  htmlFor="title"
                  className="block text-sm font-medium text-gray-700"
                >
                  Title
                </label>
                <input
                  type="text"
                  name="title"
                  id="title"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>
              <div className="mb-4">
                <label
                  htmlFor="isbn"
                  className="block text-sm font-medium text-gray-700"
                >
                  ISBN
                </label>
                <input
                  type="text"
                  name="isbn"
                  id="isbn"
                  value={isbn}
                  onChange={(e) => setIsbn(e.target.value)}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>
              <div className="mb-4">
                <label
                  htmlFor="authorIds"
                  className="block text-sm font-medium text-gray-700"
                >
                  Author IDs (comma-separated)
                </label>
                <input
                  type="text"
                  name="authorIds"
                  id="authorIds"
                  value={authorIds.join(",")}
                  onChange={(e) =>
                    setAuthorIds(
                      e.target.value.split(",").map((id) => Number(id.trim()))
                    )
                  }
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>
              <div className="mb-4">
                <label
                  htmlFor="publisherId"
                  className="block text-sm font-medium text-gray-700"
                >
                  Publisher ID
                </label>
                <input
                  type="number"
                  name="publisherId"
                  id="publisherId"
                  value={publisherId}
                  onChange={(e) => setPublisherId(Number(e.target.value))}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>
              <div className="mb-4">
                <label
                  htmlFor="categoryId"
                  className="block text-sm font-medium text-gray-700"
                >
                  Category ID
                </label>
                <input
                  type="number"
                  name="categoryId"
                  id="categoryId"
                  value={categoryId}
                  onChange={(e) => setCategoryId(Number(e.target.value))}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  required
                />
              </div>
            </div>
            <div className="items-center px-4 py-3 sm:flex sm:flex-row-reverse">
              <button
                type="submit"
                className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-green-600 text-base font-medium text-white hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 sm:ml-3 sm:w-auto sm:text-sm"
              >
                Create Book
              </button>
              <button
                type="button"
                onClick={onClose}
                className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:w-auto sm:text-sm"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreateBookModal;
