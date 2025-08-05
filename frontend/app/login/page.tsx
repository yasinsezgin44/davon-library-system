import { AuthIllustration } from "@/components/auth-illustration"
import { LoginForm } from "@/components/login-form"
import { BookOpen } from "lucide-react"

export default function LoginPage() {
  return (
    <div className="min-h-screen bg-clean-white flex">
      {/* Left Side - Illustration */}
      <div className="hidden lg:flex lg:w-1/2 relative">
        <AuthIllustration />
      </div>

      {/* Right Side - Login Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          {/* Mobile Logo */}
          <div className="lg:hidden flex items-center justify-center mb-8">
            <div className="flex items-center space-x-3">
              <div className="p-2 bg-modern-teal rounded-lg">
                <BookOpen className="h-6 w-6 text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold text-dark-gray">Davon Library</h1>
                <p className="text-dark-gray/70 text-sm">Digital Knowledge Hub</p>
              </div>
            </div>
          </div>

          {/* Login Form Card */}
          <div className="bg-white/60 backdrop-blur-sm border border-white/20 shadow-xl rounded-2xl p-8">
            <LoginForm />
          </div>

          {/* Footer Links */}
          <div className="mt-8 text-center space-x-6 text-sm text-dark-gray/60">
            <button className="hover:text-dark-gray transition-colors">Privacy Policy</button>
            <button className="hover:text-dark-gray transition-colors">Terms of Service</button>
            <button className="hover:text-dark-gray transition-colors">Help</button>
          </div>
        </div>
      </div>
    </div>
  )
}
