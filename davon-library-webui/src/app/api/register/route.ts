import { NextResponse } from "next/server";

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { name, email, password } = body;

    // --- Placeholder for your actual registration logic ---
    console.log("--- Registering User (API Placeholder) ---");
    console.log("Name:", name);
    console.log("Email:", email);
    // IMPORTANT: Never log passwords in production!
    console.log("Password:", password);

    // TODO:
    // 1. Validate input (name, email format, password strength)
    // 2. Check if email already exists in your database
    // 3. Hash the password securely (e.g., using bcrypt)
    // 4. Save the user (name, email, hashed password) to your database
    // 5. Handle any database errors

    // Simulate finding/creating a user record
    const simulatedUser = {
      id: Date.now().toString(), // Use a real ID from your DB
      name: name,
      email: email,
    };
    // --- End Placeholder ---

    // On success, return the user data (without password!)
    return NextResponse.json({ user: simulatedUser }, { status: 201 }); // 201 Created
  } catch (error: any) {
    console.error("Registration API Error:", error);
    // Return a generic error message
    return NextResponse.json(
      { message: "An error occurred during registration." },
      { status: 500 } // Internal Server Error
    );
  }
}
