"use client";
import React, { useState, useEffect } from "react";
import apiClient from "../lib/apiClient";
import { useAuth } from "../context/AuthContext";

const CategoryScroller = () => {
  const [categories, setCategories] = useState([]);
  const { isAuthReady } = useAuth();

  useEffect(() => {
    if (!isAuthReady) return;
    const fetchCategories = async () => {
      try {
        const response = await apiClient.get("/books/genres");
        setCategories(response.data);
      } catch (error) {
        console.error("Failed to fetch categories:", error);
      }
    };
    fetchCategories();
  }, [isAuthReady]);

  return (
    <div className="py-4">
      <h2 className="text-2xl font-bold mb-4">Browse by Category</h2>
      <div className="flex space-x-4 overflow-x-auto pb-4">
        {categories.map((category) => (
          <div key={category.id} className="flex-shrink-0">
            <a
              href="#"
              className="block bg-gray-700 text-white hover:bg-gray-600 rounded-full px-4 py-2 font-semibold shadow-md transition-colors duration-300"
            >
              {category.name}
            </a>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CategoryScroller;
