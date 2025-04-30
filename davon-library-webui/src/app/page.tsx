import Image from "next/image";
import HeroSection from "./components/HeroSection/HeroSection"; // Use relative path
import FeaturesSection from "./components/FeaturesSection/FeaturesSection"; // Use relative path

export default function Home() {
  return (
    <>
      {" "}
      <HeroSection />
      <FeaturesSection />
    </>
  );
}
