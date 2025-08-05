"use client"

import { BookOpen, Facebook, Twitter, Instagram, Linkedin, Mail, Phone, MapPin } from "lucide-react"
import Link from "next/link"
import { cn } from "@/lib/utils"

const navigationLinks = [
  { label: "Home", href: "/" },
  { label: "Catalog", href: "/catalog" },
  { label: "Dashboard", href: "/dashboard" },
  { label: "About Us", href: "/about" },
  { label: "Contact", href: "/contact" },
  { label: "Events", href: "/events" },
]

const legalLinks = [
  { label: "Privacy Policy", href: "/privacy" },
  { label: "Terms of Service", href: "/terms" },
  { label: "Cookie Policy", href: "/cookies" },
  { label: "Accessibility", href: "/accessibility" },
  { label: "Library Rules", href: "/rules" },
  { label: "FAQ", href: "/faq" },
]

const socialLinks = [
  { label: "Facebook", href: "https://facebook.com", icon: Facebook },
  { label: "Twitter", href: "https://twitter.com", icon: Twitter },
  { label: "Instagram", href: "https://instagram.com", icon: Instagram },
  { label: "LinkedIn", href: "https://linkedin.com", icon: Linkedin },
]

interface LibraryFooterProps {
  className?: string
}

export function LibraryFooter({ className }: LibraryFooterProps) {
  const currentYear = new Date().getFullYear()

  return (
    <footer className={cn("bg-scholarly-navy text-white", className)}>
      {/* Main Footer Content */}
      <div className="max-w-7xl mx-auto px-4 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand Section */}
          <div className="lg:col-span-1">
            <div className="flex items-center space-x-3 mb-6">
              <div className="p-2 bg-modern-teal rounded-lg">
                <BookOpen className="h-6 w-6 text-white" />
              </div>
              <div>
                <h3 className="text-xl font-bold">Davon Library</h3>
                <p className="text-white/70 text-sm">Knowledge for Everyone</p>
              </div>
            </div>
            <p className="text-white/80 text-sm leading-relaxed mb-6">
              Discover, learn, and grow with our comprehensive collection of books, digital resources, and community
              programs designed to inspire lifelong learning.
            </p>

            {/* Contact Info */}
            <div className="space-y-2">
              <div className="flex items-center space-x-2 text-sm text-white/70">
                <MapPin className="h-4 w-4" />
                <span>123 Library Street, City, State 12345</span>
              </div>
              <div className="flex items-center space-x-2 text-sm text-white/70">
                <Phone className="h-4 w-4" />
                <span>(555) 123-4567</span>
              </div>
              <div className="flex items-center space-x-2 text-sm text-white/70">
                <Mail className="h-4 w-4" />
                <span>info@davonlibrary.com</span>
              </div>
            </div>
          </div>

          {/* Navigation Links */}
          <div>
            <h4 className="text-lg font-semibold mb-6">Navigation</h4>
            <ul className="space-y-3">
              {navigationLinks.map((link) => (
                <li key={link.href}>
                  <Link
                    href={link.href}
                    className="text-white/70 hover:text-white transition-colors duration-200 text-sm hover:translate-x-1 transform inline-block"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Legal Links */}
          <div>
            <h4 className="text-lg font-semibold mb-6">Legal</h4>
            <ul className="space-y-3">
              {legalLinks.map((link) => (
                <li key={link.href}>
                  <Link
                    href={link.href}
                    className="text-white/70 hover:text-white transition-colors duration-200 text-sm hover:translate-x-1 transform inline-block"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Social Media & Newsletter */}
          <div>
            <h4 className="text-lg font-semibold mb-6">Connect With Us</h4>

            {/* Social Links */}
            <div className="flex space-x-4 mb-6">
              {socialLinks.map((social) => {
                const Icon = social.icon
                return (
                  <a
                    key={social.label}
                    href={social.href}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="p-2 bg-white/10 rounded-lg hover:bg-modern-teal transition-all duration-200 hover:scale-110 transform"
                    aria-label={social.label}
                  >
                    <Icon className="h-5 w-5" />
                  </a>
                )
              })}
            </div>

            {/* Newsletter Signup */}
            <div>
              <p className="text-white/80 text-sm mb-3">Stay updated with our latest news and events</p>
              <div className="flex">
                <input
                  type="email"
                  placeholder="Enter your email"
                  className="flex-1 px-3 py-2 bg-white/10 border border-white/20 rounded-l-lg text-white placeholder-white/50 text-sm focus:outline-none focus:ring-2 focus:ring-modern-teal focus:border-transparent"
                />
                <button className="px-4 py-2 bg-modern-teal hover:bg-modern-teal/90 rounded-r-lg transition-colors duration-200 text-sm font-medium">
                  Subscribe
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Bar */}
      <div className="border-t border-white/10">
        <div className="max-w-7xl mx-auto px-4 lg:px-8 py-6">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div className="text-white/70 text-sm">© {currentYear} Davon Library. All rights reserved.</div>

            {/* Additional Links */}
            <div className="flex items-center space-x-6 text-sm">
              <Link href="/sitemap" className="text-white/70 hover:text-white transition-colors duration-200">
                Sitemap
              </Link>
              <Link href="/support" className="text-white/70 hover:text-white transition-colors duration-200">
                Support
              </Link>
              <div className="text-white/50">Made with ❤️ for book lovers</div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  )
}
