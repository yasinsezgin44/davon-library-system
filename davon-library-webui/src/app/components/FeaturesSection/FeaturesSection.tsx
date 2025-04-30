import styles from "./FeaturesSection.module.css";
import Link from "next/link";

export default function FeaturesSection() {
  return (
    <section id="features" className={styles.featuresSection}>
      <h2 className={styles.sectionTitle}>Our Library Features</h2>
      <div className={styles.featuresContainer}>
        {/* Feature 1 */}
        <div className={styles.featureCard}>
          <div className={styles.featureIcon}>
            <i className="fas fa-book"></i>{" "}
            {/* You'll need to add Font Awesome or another icon library */}
          </div>
          <div className={styles.featureContent}>
            <h3 className={styles.featureTitle}>Extensive Collection</h3>
            <p className={styles.featureDescription}>
              Access over 50,000 books, journals, and digital media across all
              subjects and interests.
            </p>
            <Link href="#" className={styles.featureLink}>
              Browse Catalog
            </Link>
          </div>
        </div>

        {/* Feature 2 */}
        <div className={styles.featureCard}>
          <div className={styles.featureIcon}>
            <i className="fas fa-laptop"></i>
          </div>
          <div className={styles.featureContent}>
            <h3 className={styles.featureTitle}>Digital Resources</h3>
            <p className={styles.featureDescription}>
              Enjoy 24/7 access to e-books, research databases, and online
              learning materials.
            </p>
            <Link href="#" className={styles.featureLink}>
              Explore Digital Library
            </Link>
          </div>
        </div>

        {/* Feature 3 */}
        <div className={styles.featureCard}>
          <div className={styles.featureIcon}>
            <i className="fas fa-users"></i>
          </div>
          <div className={styles.featureContent}>
            <h3 className={styles.featureTitle}>Community Programs</h3>
            <p className={styles.featureDescription}>
              Participate in book clubs, workshops, and educational events for
              all ages.
            </p>
            <Link href="#" className={styles.featureLink}>
              View Calendar
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
}
