import Link from "next/link";
import styles from "./HeroSection.module.css";

export default function HeroSection() {
  return (
    <section className={styles.heroSection}>
      <div className={styles.heroContent}>
        <h2 className={styles.heroTitle}>Welcome to Davon Library System</h2>
        <p className={styles.heroDescription}>
          Discover a world of knowledge at your fingertips. Our modern library
          system offers extensive collections, digital resources, and innovative
          services to support your learning journey.
        </p>
        <a href="#features" className={styles.ctaButton}>
          Explore Our Features
        </a>
      </div>
    </section>
  );
}
