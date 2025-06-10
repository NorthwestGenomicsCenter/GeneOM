import {jwtDecode} from 'jwt-decode'

// composables/useAuth.ts
export const useAuth = () => {
	const isAuthenticated = useState('isAuthenticated', () => false)
	
	const getToken = (): any => {
		const cookie = useCookie('refresh_token');
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
	
	const setAuthCookies = (accessToken : string, refreshToken : string) => {
		const accessTokenCookie = useCookie('access_token', {
			httpOnly: false, // Set to true if you want more security (but won't be accessible via JS)
			secure: process.env.NODE_ENV === 'production', // HTTPS only in production
			sameSite: 'strict',
			maxAge: 60 * 10, // 10 minute // expiry
			path: '/'
		})
		
		if (accessToken !== accessTokenCookie.value) {
			accessTokenCookie.value = accessToken
		}
		
		const refreshTokenCookie = useCookie('refresh_token', {
			httpOnly: false, // Set to true if you want more security (but won't be accessible via JS)
			secure: process.env.NODE_ENV === 'production', // HTTPS only in production
			sameSite: 'strict',
			maxAge: 60 * 60 * 2, // 2 hour expiry
			path: '/'
		})
		
		if (refreshToken !== refreshTokenCookie.value) {
			refreshTokenCookie.value = refreshToken
		}
	}
	
	interface LoginResponse {
		access_token: string;
		refresh_token: string;
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
			setAuthCookies(loginResponse.access_token, loginResponse.refresh_token)
			
			isAuthenticated.value = true;
			user.value = getToken();
		}
		
		return {data, error, isAuthenticated}
	}
	
	const logout = async () => {
		const accessCookie = useCookie('access_token');
		accessCookie.value = null;
		const refreshCookie = useCookie('refresh_token');
		refreshCookie.value = null;
		
		isAuthenticated.value = false;
		user.value = null;
		
		const router = useRouter();
		await router.push({path: "/login"})
	}
	
	const user = ref(getToken())
	
	const expire = computed(() => {
		const expire = new Date(getToken().exp * 1000);
		const now = new Date();
		const delay = expire.getTime() - now.getTime();
		
		if (delay <= 0) {
			// If the target date is in the past, execute the callback immediately
			logout();
		} else {
			setTimeout(logout, delay);
		}
		
		return delay
	})
	
	// return { isAuthenticated, checkAuth, login, logout, getToken, user }
	return {login, logout, getToken, setAuthCookies, isAuthenticated, user, expire}
}