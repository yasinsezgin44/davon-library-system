import styles from "./Header.module.css";
import Image from "next/image";

export default function Header() {
    return (
        <header className="header-container">
            <div className="logo-title-container">
                <img src="images/logo.png" alt="Davon Library Logo" className="library-logo">
                <h1 className={styles.siteTitle}>Davon Library</h1>
            </div>

            <input type="checkbox" id="menu-toggle" className="menu-toggle" />
            <label htmlFor="menu-toggle" className="menu-icon">☰</label>

            <nav>
                <ul className="nav-menu">
                    <li className="nav-item"><a href="#home" className="nav-link">Home</a></li>
                    <li className="nav-item"><a href="#about" className="nav-link">About</a></li>
                    <li className="nav-item"><a href="#services" className="nav-link">Services</a></li>
                    <li className="nav-item"><a href="#collection" className="nav-link">Collection</a></li>
                    <li className="nav-item"><a href="#events" className="nav-link">Events</a></li>
                    <li className="nav-item"><a href="#contact" className="nav-link">Contact</a></li>
                </ul>
            </nav>
        </header>
    );
}
