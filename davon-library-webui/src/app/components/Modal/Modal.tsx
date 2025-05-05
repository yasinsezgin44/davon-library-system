"use client"; // Modals often need client-side interaction

import React, { useEffect, useRef } from "react";
import styles from "./Modal.module.css";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  title?: string;
}

export default function Modal({
  isOpen,
  onClose,
  children,
  title,
}: ModalProps) {
  const modalRef = useRef<HTMLDivElement>(null);
  const previousFocusElement = useRef<HTMLElement | null>(null);

  // Close modal on Escape key press and manage focus
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
        return;
      }

      // Focus trap for Tab key
      if (event.key === "Tab" && modalRef.current) {
        const focusableElements = modalRef.current.querySelectorAll(
          'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );

        if (focusableElements.length === 0) return;

        const firstElement = focusableElements[0] as HTMLElement;
        const lastElement = focusableElements[
          focusableElements.length - 1
        ] as HTMLElement;

        // Shift+Tab from first element should go to last element
        if (event.shiftKey && document.activeElement === firstElement) {
          lastElement.focus();
          event.preventDefault();
        }
        // Tab from last element should go to first element
        else if (!event.shiftKey && document.activeElement === lastElement) {
          firstElement.focus();
          event.preventDefault();
        }
      }
    };

    if (isOpen) {
      // Store current active element to restore focus later
      previousFocusElement.current = document.activeElement as HTMLElement;

      // Prevent page scrolling when modal is open
      document.body.style.overflow = "hidden";

      document.addEventListener("keydown", handleKeyDown);
      // Optional: Focus management - focus first focusable element inside modal
      setTimeout(() => {
        if (modalRef.current) {
          // Focus the modal or the first focusable element
          const firstFocusable = modalRef.current.querySelector(
            'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
          ) as HTMLElement;

          if (firstFocusable) {
            firstFocusable.focus();
          } else {
            modalRef.current.focus();
          }
        }
      }, 50);
    }

    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      // Restore page scrolling when modal is closed
      document.body.style.overflow = "";

      // Return focus to previous element when modal closes
      if (isOpen && previousFocusElement.current) {
        setTimeout(() => {
          previousFocusElement.current?.focus();
        }, 0);
      }
    };
  }, [isOpen, onClose]);

  // Close modal when clicking the overlay
  const handleOverlayClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget) {
      onClose();
    }
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div
      className={styles.modalOverlay}
      onClick={handleOverlayClick}
      role="dialog"
      aria-modal="true"
      aria-labelledby={title ? "modal-title" : undefined}
      aria-describedby="modal-description"
    >
      <div
        className={styles.modalContent}
        ref={modalRef}
        tabIndex={-1}
        aria-live="polite"
      >
        <button
          className={styles.closeButton}
          onClick={onClose}
          aria-label="Close modal"
          type="button"
        >
          <span aria-hidden="true">&times;</span>
        </button>
        {title && (
          <h2 id="modal-title" className={styles.modalTitle}>
            {title}
          </h2>
        )}
        <div id="modal-description">{children}</div>
      </div>
    </div>
  );
}
