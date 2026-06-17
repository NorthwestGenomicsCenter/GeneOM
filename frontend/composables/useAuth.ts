import {jwtDecode} from 'jwt-decode'

let logoutTimer: ReturnType<typeof setTimeout> | null = null;

// composables/useAuth.ts
export const useAuth = () => {
	const isAuthenticated = computed(() => {
		const cookie = useCookie('refreshToken');
		const token = cookie.value;

		try {
			jwtDecode(token)
			return true;
		} catch (error) {
			console.error('Failed to decode JWT:', error);
			return false;
		}
	});
	
	const getToken = (): any => {
		const cookie = useCookie('refreshToken');
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
		const accessTokenCookie = useCookie('accessToken', {
			httpOnly: false, // Set to true if you want more security (but won't be accessible via JS)
			secure: process.env.NODE_ENV === 'production', // HTTPS only in production
			sameSite: 'strict',
			maxAge: 60 * 10, // 10 minute // expiry
			path: '/'
		})
		
		if (accessToken !== accessTokenCookie.value) {
			accessTokenCookie.value = accessToken
		}
		
		const refreshTokenCookie = useCookie('refreshToken', {
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
	
	const login = async (username: string, password: string) => {
		const credentials = {
            username: username,
            password: password
        }

        const {data, error}: { data: any, error: any } = await useApi('/auth/login', {
			method: 'POST',
			body: credentials,
			credentials: 'include'
		})

        let isFirstLogin;
		if (data.value && !error.value) {
			const loginResponse: LoginResponseDTO = data.value;
			
			// Set the token from response data
			setAuthCookies(loginResponse.accessToken, loginResponse.refreshToken)

			user.value = getToken();
		}

        isFirstLogin = user.value.firstLogin;
		
		return {data, error, isAuthenticated, isFirstLogin}
	}
	
	const logout = async () => {
		const accessCookie = useCookie('accessToken');
		accessCookie.value = null;
		const refreshCookie = useCookie('refreshToken');
		refreshCookie.value = null;

		user.value = null;
		
		const router = useRouter();
		await router.push({path: "/login"})
	}
	
	const user = ref(getToken())

	const scheduleLogout = () => {
		// Clear any existing timer to avoid duplicates
		if (logoutTimer) {
			clearTimeout(logoutTimer)
			logoutTimer = null
		}

		const tokenData = getToken();
		if (!tokenData?.exp) return;

		const expiry = new Date(tokenData.exp * 1000);
		const delay = expiry.getTime() - Date.now() - 7000000;
		console.log("SCHEDULING with delay " + delay)

		if (delay <= 0) {
			// Token is already expired
			logout();
		} else {
			logoutTimer = setTimeout(logout, delay)
		}
	}
	
	// return { isAuthenticated, checkAuth, login, logout, getToken, user }
	return {login, logout, getToken, setAuthCookies, isAuthenticated, user, scheduleLogout}
}