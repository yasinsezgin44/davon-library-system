import Image from "next/image";
import HeroSection from "./components/HeroSection/HeroSection";
import FeatureSection from "./components/FeatureSection/FeatureSection";

export default function Home() {
  return (
    <>
      {" "}
      {/* Use a fragment to return multiple top-level elements */}
      <HeroSection />
      {/* We will add Features section below this */}
      <FeatureSection />
    </>
  );
}
