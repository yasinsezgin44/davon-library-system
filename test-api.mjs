import axios from 'axios';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8083/api';

const testEndpoints = async () => {
  try {
    console.log('--- Registering new user ---');
    await axios.post(`${BASE_URL}/auth/register`, {
      username: 'testuser',
      password: 'password',
      fullName: 'Test User',
      email: 'testuser@example.com',
    });
    console.log('User registered successfully');
  } catch (error) {
    console.error('Error registering user:', error.message);
  }

  try {
    console.log('\n--- Testing GET /books/trending ---');
    const trendingBooks = await axios.get(`${BASE_URL}/books/trending`);
    console.log('Status:', trendingBooks.status);
    console.log('Data:', trendingBooks.data);
  } catch (error) {
    console.error('Error fetching trending books:', error.message);
  }

  try {
    console.log('\n--- Testing GET /books/genres ---');
    const genres = await axios.get(`${BASE_URL}/books/genres`);
    console.log('Status:', genres.status);
    console.log('Data:', genres.data);
  } catch (error) {
    console.error('Error fetching genres:', error.message);
  }

  try {
    console.log('\n--- Testing POST /auth/login ---');
    const login = await axios.post(`${BASE_URL}/auth/login`, {
      username: 'testuser',
      password: 'password',
    });
    console.log('Status:', login.status);
    console.log('Data:', login.data);
  } catch (error) {
    console.error('Error logging in:', error.message);
  }

  // The following tests require an auth token
  // I will add them after I have a valid token from the login test
};

testEndpoints();

