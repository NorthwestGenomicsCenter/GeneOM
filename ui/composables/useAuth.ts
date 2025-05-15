import {jwtDecode} from 'jwt-decode'

// composables/useAuth.ts
export const useAuth = () => {
	const isAuthenticated = useState('isAuthenticated', () => false)
	
	const getToken = (): any => {
		const cookie = useCookie('access_token');
		const token = cookie.value;
		
		if (!token) {
			return {
				isAuthenticated: false,
			};
		}
		
		try {
			return {...jwtDecode(token), isAuthenticated: true};
		} catch (error) {
			console.error('Failed to decode JWT:', error);
			return null;
		}
	};
	
	const setAuthCookie = (token: string) => {
		const cookie = useCookie('access_token', {
			httpOnly: false, // Set to true if you want more security (but won't be accessible via JS)
			secure: process.env.NODE_ENV === 'production', // HTTPS only in production
			sameSite: 'strict',
			maxAge: 60 * 60 * 24 * 7, // 1 week expiry
			path: '/'
		})
		
		cookie.value = token
	}
	
	interface LoginResponse {
		token: string;
		type: string;
		id: number;
		username: string;
		roles: any[];
	}
	
	const login = async (credentials: { username: string, password: string }) => {
		const {data, error}: { data: any, error: any } = await useFetch('/api/auth/login', {
			method: 'POST',
			body: credentials,
			credentials: 'include' // Send/receive cookies
		})
		
		if (data.value && !error.value) {
			const loginResponse: LoginResponse = data.value;
			
			// Set the token from response data
			setAuthCookie(loginResponse.token)
			
			isAuthenticated.value = true;
			user.value = getToken();
		}
		
		return {data, error, isAuthenticated}
	}
	
	const logout = async () => {
		const cookie = useCookie('access_token');
		cookie.value = null;
	}
	
	const user = ref(getToken())
	
	// return { isAuthenticated, checkAuth, login, logout, getToken, user }
	return {login, logout, getToken, isAuthenticated, user}
}