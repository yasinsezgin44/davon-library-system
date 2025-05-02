"use client";

import styles from "./Header.module.css";
import Image from "next/image";
import Link from "next/link";
import { useState, useEffect } from "react";
import { usePathname } from "next/navigation";

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const pathname = usePathname();

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  useEffect(() => {
    setIsMenuOpen(false);
  }, [pathname]);

  const isActive = (href: string) => {
    return pathname === href || (href !== "/" && pathname.startsWith(href));
  };

  return (
    <header className={styles.headerContainer}>
      <Link href="/" className={styles.logoTitleContainerLink}>
        <div className={styles.logoTitleContainer}>
          <Image
            src="/logo.png"
            alt="Davon Library Logo"
            width={80}
            height={50}
            className={styles.libraryLogo}
            priority
          />
          <h1 className={styles.siteTitle}>Davon Library</h1>
        </div>
      </Link>

      <button
        className={`${styles.menuIcon} ${
          isMenuOpen ? styles.menuIconOpen : ""
        }`}
        onClick={toggleMenu}
        aria-label="Toggle menu"
        aria-expanded={isMenuOpen}
      >
        <span className={styles.hamburgerLine}></span>
        <span className={styles.hamburgerLine}></span>
        <span className={styles.hamburgerLine}></span>
      </button>

      <nav className={`${styles.mainNav} ${isMenuOpen ? styles.navOpen : ""}`}>
        <ul className={styles.navMenu}>
          <li className={styles.navItem}>
            <Link
              href="/"
              className={`${styles.navLink} ${
                isActive("/") ? styles.active : ""
              }`}
            >
              Home
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link
              href="/about"
              className={`${styles.navLink} ${
                isActive("/about") ? styles.active : ""
              }`}
            >
              About
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link
              href="/services"
              className={`${styles.navLink} ${
                isActive("/services") ? styles.active : ""
              }`}
            >
              Services
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link
              href="/collection"
              className={`${styles.navLink} ${
                isActive("/collection") ? styles.active : ""
              }`}
            >
              Collection
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link
              href="/events"
              className={`${styles.navLink} ${
                isActive("/events") ? styles.active : ""
              }`}
            >
              Events
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link
              href="/contact"
              className={`${styles.navLink} ${
                isActive("/contact") ? styles.active : ""
              }`}
            >
              Contact
            </Link>
          </li>
        </ul>
      </nav>
    </header>
  );
}
