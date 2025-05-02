import styles from "./Footer.module.css"; // Import the CSS module
import Link from "next/link"; // Import Link for internal links

export default function Footer() {
  const currentYear = new Date().getFullYear(); // Get current year

  return (
    <footer className={styles.footer}>
      <div className={styles.footerContainer}>
        {/* Contact Us Section */}
        <div className={styles.footerSection}>
          <h3 className={styles.footerTitle}>Contact Us</h3>
          <ul className={styles.contactInfo}>
            <li>
              <i className="fas fa-map-marker-alt"></i> 123 Library Street,
              Davon City
            </li>
            <li>
              <i className="fas fa-phone"></i> (555) 123-4567
            </li>
            <li>
              <i className="fas fa-envelope"></i> info@davonlibrary.com
            </li>
            <li>
              <i className="fas fa-clock"></i> Mon-Fri: 9AM-8PM, Sat-Sun:
              10AM-6PM
            </li>
          </ul>
        </div>

        {/* Quick Links Section */}
        <div className={styles.footerSection}>
          <h3 className={styles.footerTitle}>Quick Links</h3>
          <ul className={styles.quickLinks}>
            <li>
              <Link href="/about" className={styles.footerLink}>
                About Us
              </Link>
            </li>
            <li>
              <Link href="/services" className={styles.footerLink}>
                Services
              </Link>
            </li>
            <li>
              <Link href="/membership" className={styles.footerLink}>
                Membership
              </Link>
            </li>
            <li>
              <Link href="/events" className={styles.footerLink}>
                Events
              </Link>
            </li>
          </ul>
        </div>

        {/* Connect With Us Section */}
        <div className={styles.footerSection}>
          <h3 className={styles.footerTitle}>Connect With Us</h3>
          <p className={styles.connectText}>
            Join our community and stay updated with the latest events and
            additions to our collection.
          </p>
          <div className={styles.socialLinks}>
            <a
              href="https://facebook.com"
              target="_blank"
              rel="noopener noreferrer"
              className={styles.socialLink}
              aria-label="Facebook"
            >
              <i className="fab fa-facebook-f"></i>
            </a>
            <a
              href="https://twitter.com"
              target="_blank"
              rel="noopener noreferrer"
              className={styles.socialLink}
              aria-label="Twitter"
            >
              <i className="fab fa-twitter"></i>
            </a>
            <a
              href="https://instagram.com"
              target="_blank"
              rel="noopener noreferrer"
              className={styles.socialLink}
              aria-label="Instagram"
            >
              <i className="fab fa-instagram"></i>
            </a>
            <a
              href="https://youtube.com"
              target="_blank"
              rel="noopener noreferrer"
              className={styles.socialLink}
              aria-label="YouTube"
            >
              <i className="fab fa-youtube"></i>
            </a>
          </div>
        </div>
      </div>

      {/* Copyright Section */}
      <div className={styles.copyright}>
        &copy; {currentYear} Davon Library System. All Rights Reserved.
      </div>
    </footer>
  );
}
