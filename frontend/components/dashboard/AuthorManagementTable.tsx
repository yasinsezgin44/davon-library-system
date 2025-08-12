"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";
import CreateAuthorModal from "./CreateAuthorModal";
import UpdateAuthorModal from "./UpdateAuthorModal";
import DeleteConfirmationModal from "./DeleteConfirmationModal";

export type Author = {
  id: number;
  name: string;
};

const AuthorManagementTable = () => {
  const [authors, setAuthors] = useState<Author[]>([]);
  const [loading, setLoading] = useState(true);
  const [isCreateModalOpen, setCreateModalOpen] = useState(false);
  const [isUpdateModalOpen, setUpdateModalOpen] = useState(false);
  const [isDeleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedAuthor, setSelectedAuthor] = useState<Author | null>(null);

  useEffect(() => {
    const fetchAuthors = async () => {
      setLoading(true);
      try {
        const response = await apiClient.get("/authors");
        setAuthors(response.data);
      } catch (error) {
        console.error("Failed to fetch authors:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchAuthors();
  }, []);

  const handleCreate = async (authorData: Partial<Author>) => {
    try {
      const response = await apiClient.post("/authors", authorData);
      setAuthors([...authors, response.data]);
      setCreateModalOpen(false);
    } catch (error) {
      console.error("Failed to create author:", error);
    }
  };

  const handleUpdate = async (id: number, authorData: Partial<Author>) => {
    try {
      const response = await apiClient.put(`/authors/${id}`, authorData);
      setAuthors(
        authors.map((author) => (author.id === id ? response.data : author))
      );
      setUpdateModalOpen(false);
      setSelectedAuthor(null);
    } catch (error) {
      console.error("Failed to update author:", error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await apiClient.delete(`/authors/${id}`);
      setAuthors(authors.filter((author) => author.id !== id));
      setDeleteModalOpen(false);
      setSelectedAuthor(null);
    } catch (error) {
      console.error("Failed to delete author:", error);
    }
  };

  const openUpdateModal = (author: Author) => {
    setSelectedAuthor(author);
    setUpdateModalOpen(true);
  };

  const openDeleteModal = (author: Author) => {
    setSelectedAuthor(author);
    setDeleteModalOpen(true);
  };

  if (loading) {
    return (
      <div className="text-center py-10">
        <p>Loading authors...</p>
      </div>
    );
  }

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden">
      <div className="flex justify-between items-center mb-4 px-6 py-4">
        <h2 className="text-2xl font-bold text-gray-800">Author Management</h2>
        <button
          onClick={() => setCreateModalOpen(true)}
          className="px-4 py-2 rounded-md font-semibold text-sm bg-green-500 text-white hover:bg-green-600"
        >
          Add New Author
        </button>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Name
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {authors.map((author) => (
              <tr key={author.id}>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {author.name}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <div className="flex items-center">
                    <button
                      onClick={() => openUpdateModal(author)}
                      className="px-4 py-2 rounded-md font-semibold text-sm bg-indigo-500 text-white hover:bg-indigo-600 mr-2"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => openDeleteModal(author)}
                      className="px-4 py-2 rounded-md font-semibold text-sm bg-red-500 text-white hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <CreateAuthorModal
        isOpen={isCreateModalOpen}
        onClose={() => setCreateModalOpen(false)}
        onCreate={handleCreate}
      />
      <UpdateAuthorModal
        isOpen={isUpdateModalOpen}
        onClose={() => {
          setUpdateModalOpen(false);
          setSelectedAuthor(null);
        }}
        onUpdate={handleUpdate}
        author={selectedAuthor}
      />
      <DeleteConfirmationModal
        isOpen={isDeleteModalOpen}
        onClose={() => {
          setDeleteModalOpen(false);
          setSelectedAuthor(null);
        }}
        onConfirm={() => {
          if (selectedAuthor) {
            handleDelete(selectedAuthor.id);
          }
        }}
        itemName={selectedAuthor ? selectedAuthor.name : ""}
      />
    </div>
  );
};

export default AuthorManagementTable;
