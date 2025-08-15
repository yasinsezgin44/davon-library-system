"use client";

import { useState, useEffect } from "react";
import { Author } from "./AuthorManagementTable";

interface UpdateAuthorModalProps {
  isOpen: boolean;
  onClose: () => void;
  onUpdate: (id: number, authorData: Partial<Author>) => void;
  author: Author | null;
}

const UpdateAuthorModal = ({
  isOpen,
  onClose,
  onUpdate,
  author,
}: UpdateAuthorModalProps) => {
  const [authorData, setAuthorData] = useState<Partial<Author>>({});

  useEffect(() => {
    if (author) {
      setAuthorData(author);
    }
  }, [author]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setAuthorData({ ...authorData, [name]: value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (author) {
      onUpdate(author.id, authorData);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="mt-3 text-center">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Edit Author
          </h3>
          <form className="mt-2" onSubmit={handleSubmit}>
            <div className="mb-4">
              <label
                htmlFor="name"
                className="block text-sm font-medium text-gray-700"
              >
                Name
              </label>
              <input
                type="text"
                name="name"
                id="name"
                value={authorData.name || ""}
                onChange={handleChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              />
            </div>
            <div className="items-center px-4 py-3">
              <button
                type="submit"
                className="px-4 py-2 bg-indigo-500 text-white text-base font-medium rounded-md w-full shadow-sm hover:bg-indigo-600 focus:outline-none focus:ring-2 focus:ring-indigo-300"
              >
                Update Author
              </button>
              <button
                type="button"
                onClick={onClose}
                className="mt-3 px-4 py-2 bg-gray-500 text-white text-base font-medium rounded-md w-full shadow-sm hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-300"
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

export default UpdateAuthorModal;
