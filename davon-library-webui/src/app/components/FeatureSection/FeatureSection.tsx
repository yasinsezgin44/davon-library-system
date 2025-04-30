import styles from "./FeatureSection.module.css";
import Link from "next/link";

export default function FeatureSection() {
    return (
        <section className="features-section">
            <h2 className="section-title">Our Library Features</h2>
            <div className="features-container">

                {/* Feature 1 */}
                <div className="feature-card">
                    <div className="feature-icon">
                        <i className="fas fa-book"></i> <!-- You'll need to add Font Awesome or another icon library -->
                    </div>
                    <div className="feature-content">
                        <h3 className="feature-title">Extensive Collection</h3>
                        <p className="feature-description">
                            Access over 50,000 books, journals, and digital media across all subjects and interests.
                        </p>
                        <a href="#" className="feature-link">Browse Catalog</a>
                    </div>
                </div>
        
                {/* Feature 2 */}
                <div className="feature-card">
                    <div className="feature-icon">
                        <i className="fas fa-laptop"></i>
                    </div>
                    <div className="feature-content">
                        <h3 className="feature-title">Digital Resources</h3>
                        <p className="feature-description">
                            Enjoy 24/7 access to e-books, research databases, and online learning materials.
                        </p>
                        <a href="#" className="feature-link">Explore Digital Library</a>
                    </div>
                </div>
        
               {/* Feature 3 */}
                <div className="feature-card">
                    <div className="feature-icon">
                        <i className="fas fa-users"></i>
                    </div>
                    <div className="feature-content">
                        <h3 className="feature-title">Community Programs</h3>
                        <p className="feature-description">
                            Participate in book clubs, workshops, and educational events for all ages.
                        </p>
                        <a href="#" className="feature-link">View Calendar</a>
                    </div>
                </div>
            </div>
        </section>
    );
}