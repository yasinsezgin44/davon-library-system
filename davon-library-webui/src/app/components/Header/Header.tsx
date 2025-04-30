import styles from "./Header.module.css";
import Image from "next/image";
import Link from "next/link";

export default function Header() {
  return (
    <header className={styles.headerContainer}>
      <div className={styles.logoTitleContainer}>
        <Image
          src="/logo.png"
          alt="Davon Library Logo"
          width={80}
          height={50}
          className={styles.libraryLogo}
        />
        <h1 className={styles.siteTitle}>Davon Library</h1>
      </div>

      <input type="checkbox" id="menu-toggle" className={styles.menuToggle} />
      <label htmlFor="menu-toggle" className={styles.menuIcon}>
        ☰
      </label>

      <nav className={styles.mainNav}>
        <ul className={styles.navMenu}>
          <li className={styles.navItem}>
            <Link href="#home" className={styles.navLink}>
              Home
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link href="#about" className={styles.navLink}>
              About
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link href="#services" className={styles.navLink}>
              Services
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link href="#collection" className={styles.navLink}>
              Collection
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link href="#events" className={styles.navLink}>
              Events
            </Link>
          </li>
          <li className={styles.navItem}>
            <Link href="#contact" className={styles.navLink}>
              Contact
            </Link>
          </li>
        </ul>
      </nav>
    </header>
  );
}
