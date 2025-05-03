import { NextResponse } from "next/server";

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { email, password } = body;

    // --- Placeholder for your actual login logic ---
    console.log("--- Logging In User (API Placeholder) ---");
    console.log("Email:", email);
    // IMPORTANT: Never log passwords in production!
    // console.log("Password:", password);

    // TODO:
    // 1. Validate input (email format)
    // 2. Find the user by email in your database
    // 3. If user found, compare the provided password with the stored HASHED password using bcrypt.compare()
    // 4. If passwords match, return user data (WITHOUT the password hash)
    // 5. If user not found or passwords don't match, return an appropriate error (e.g., 401 Unauthorized)

    // --- Simulate Login ---
    // !!! VERY INSECURE - Replace with real database lookup and password hash comparison !!!
    if (email && password === "password") { // Dummy check: any email works if password is "password"
      // Simulate fetching user data from DB based on email
      const simulatedUser = {
        id: Date.now().toString(), // Use a real ID from your DB
        name: email.split("@")[0] || "User", // Generate a dummy name
        email: email,
      };
      console.log("Simulated login successful for:", email);
      return NextResponse.json({ user: simulatedUser }, { status: 200 }); // OK
    } else {
      console.log("Simulated login failed for:", email);
      // User not found or password incorrect
      return NextResponse.json(
        { message: "Invalid email or password" },
        { status: 401 } // Unauthorized
      );
    }
    // --- End Placeholder ---
  } catch (error: any) {
    console.error("Login API Error:", error);
    // Return a generic error message
    return NextResponse.json(
      { message: "An error occurred during login." },
      { status: 500 } // Internal Server Error
    );
  }
}
