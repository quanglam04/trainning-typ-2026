// Helper API Client tự động Refresh Token khi Access Token hết hạn (401 TokenExpiredError)
export const API_BASE = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_URL) 
  ? import.meta.env.VITE_API_URL 
  : 'http://localhost:3000/api';

export const getAccessToken = () => localStorage.getItem('accessToken') || '';
export const getRefreshToken = () => localStorage.getItem('refreshToken') || '';

export const setTokens = (accessToken, refreshToken) => {
  if (accessToken) localStorage.setItem('accessToken', accessToken);
  if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
};

export const clearTokens = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('user');
};

/**
 * Custom Fetch tự động xử lý:
 * 1. Gắn Header Authorization: Bearer <accessToken>
 * 2. Nếu server trả về 401 (hết hạn 5 phút):
 *    - Tự động gọi API /auth/refresh-token bằng refreshToken
 *    - Nhận accessToken mới và lưu lại vào localStorage
 *    - Tự động thực hiện lại (retry) request ban đầu
 * 3. Nếu refreshToken cũng hết hạn hoặc lỗi: Đăng xuất người dùng
 */
export const authFetch = async (url, options = {}) => {
  let token = getAccessToken();

  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  let response = await fetch(url, { ...options, headers });

  // Nếu gặp lỗi 401 (Access Token hết hạn hoặc không hợp lệ)
  if (response.status === 401) {
    const refreshToken = getRefreshToken();
    if (refreshToken) {
      try {
        console.log('🔄 Access Token hết hạn. Đang tự động refresh token...');
        const refreshRes = await fetch(`${API_BASE}/auth/refresh-token`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ refreshToken })
        });

        const refreshData = await refreshRes.json();
        if (refreshData.error || !refreshData.data?.accessToken) {
          throw new Error('Refresh token invalid or expired');
        }

        // Cập nhật Access Token mới
        const newAccessToken = refreshData.data.accessToken;
        const newRefreshToken = refreshData.data.refreshToken || refreshToken;
        setTokens(newAccessToken, newRefreshToken);

        // Gắn token mới và thực hiện lại request ban đầu
        headers['Authorization'] = `Bearer ${newAccessToken}`;
        response = await fetch(url, { ...options, headers });
      } catch (refreshErr) {
        console.warn('❌ Refresh token thất bại. Phiên đăng nhập đã hết hạn.');
        clearTokens();
        window.dispatchEvent(new Event('auth:logout'));
        throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
      }
    } else {
      clearTokens();
      window.dispatchEvent(new Event('auth:logout'));
    }
  }

  return response;
};

