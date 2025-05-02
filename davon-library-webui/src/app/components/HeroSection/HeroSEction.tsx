import Link from "next/link";
import styles from "./HeroSection.module.css";
import { useState } from "react";

export default function HeroSection() {
  const [isExpanded, setIsExpanded] = useState(false);
  return (
    <section className={styles.heroSection}>
      <div className={styles.heroContent}>
        <h2 className={styles.heroTitle}>Welcome to Davon Library System</h2>
        <p className={styles.heroDescription}>
          {isExpanded
            ? `Discover a world of knowledge at your fingertips. Our modern library system offers extensive collections, digital resources, and innovative services to support your learning journey.`
            : `Discover a world of knowledge at your fingertips...`}
        </p>
        <button
          className={styles.ctaButton}
          onClick={() => setIsExpanded(!isExpanded)}
        >
          {isExpanded ? "Show Less" : "Read More"}
        </button>
      </div>
    </section>
  );
}
