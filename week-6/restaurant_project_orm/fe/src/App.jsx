import React, { useState, useEffect } from 'react';
import { API_BASE, authFetch, setTokens, clearTokens, getAccessToken, getRefreshToken } from './api';

export default function App() {
  const [token, setToken] = useState(getAccessToken());
  const [currentUser, setCurrentUser] = useState(() => {
    const u = localStorage.getItem('user');
    return u ? JSON.parse(u) : null;
  });
  const [activeTab, setActiveTab] = useState('pos'); // 'pos' | 'kitchen' | 'cashier' | 'users' | 'inventory'
  const [tables, setTables] = useState([]);
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [orders, setOrders] = useState([]);
  const [usersList, setUsersList] = useState([]);
  const [cart, setCart] = useState([]);
  const [selectedTable, setSelectedTable] = useState(null);
  const [customerEmail, setCustomerEmail] = useState('khachhang@gmail.com');
  const [toast, setToast] = useState({ show: false, msg: '', isError: false });

  // State cho màn hình Login
  const [loginEmail, setLoginEmail] = useState('admin@restaurant.com');
  const [loginPassword, setLoginPassword] = useState('Password123@');
  const [loginError, setLoginError] = useState('');
  const [isLoggingIn, setIsLoggingIn] = useState(false);

  // Modal thêm món mới
  const [showAddProductModal, setShowAddProductModal] = useState(false);
  const [newProductData, setNewProductData] = useState({
    name: '',
    price: '',
    quantity: 20,
    category_id: '',
    description: ''
  });

  // Modal xem chi tiết món ăn
  const [selectedProductDetail, setSelectedProductDetail] = useState(null);
  const [detailQuantity, setDetailQuantity] = useState(1);

  // Xem chi tiết Đơn hàng & Bộ lọc Order
  const [selectedOrderDetail, setSelectedOrderDetail] = useState(null);
  const [orderFilterStatus, setOrderFilterStatus] = useState('all');
  const [orderSearchText, setOrderSearchText] = useState('');

  // State phân trang & thống kê Đơn Hàng (Backend Pagination)
  const [orderPage, setOrderPage] = useState(1);
  const [orderLimit, setOrderLimit] = useState(5);
  const [orderPagination, setOrderPagination] = useState({ total: 0, totalPages: 1 });
  const [orderStats, setOrderStats] = useState({ totalOrders: 0, activeCount: 0, completedCount: 0, totalRevenue: 0 });

  // State phân trang & bộ lọc Thực Đơn món ăn
  const [productPage, setProductPage] = useState(1);
  const [productLimit, setProductLimit] = useState(6);
  const [productPagination, setProductPagination] = useState({ total: 0, totalPages: 1 });
  const [selectedCategoryFilter, setSelectedCategoryFilter] = useState('all');
  const [productSearchKeyword, setProductSearchKeyword] = useState('');

  // Helper gán ảnh đẹp tự động theo tên/loại món ăn
  const getProductImage = (product) => {
    if (product?.image_url) return product.image_url;
    const name = (product?.name || '').toLowerCase();
    
    if (name.includes('wagyu') || name.includes('bò') || name.includes('beef') || name.includes('thăn')) {
      return 'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('cá hồi') || name.includes('salmon')) {
      return 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('mực') || name.includes('tôm') || name.includes('hải sản') || name.includes('seafood')) {
      return 'https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('salad')) {
      return 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('súp') || name.includes('soup') || name.includes('nấm')) {
      return 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('khoai tây') || name.includes('chiên') || name.includes('snack')) {
      return 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('tiramisu') || name.includes('panna cotta') || name.includes('bánh') || name.includes('cake') || name.includes('dessert')) {
      return 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('vang') || name.includes('wine') || name.includes('rượu')) {
      return 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=600&auto=format&fit=crop&q=80';
    }
    if (name.includes('trà') || name.includes('nước ép') || name.includes('tea') || name.includes('juice') || name.includes('uống')) {
      return 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=600&auto=format&fit=crop&q=80';
    }
    return 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600&auto=format&fit=crop&q=80';
  };

  // Xác định các Tab mà vai trò hiện tại được phép nhìn thấy (RBAC)
  const role = currentUser?.role?.toLowerCase() || '';
  const isAdmin = role === 'admin';
  const isAdminOrManager = ['admin', 'manager'].includes(role);

  const availableTabs = [
    { key: 'pos', icon: 'fa-chair', label: '1. Sơ Đồ Bàn & Gọi Món', allow: isAdminOrManager || role === 'waiter' || role === 'cashier' },
    { key: 'orders', icon: 'fa-clipboard-list', label: '2. Danh Sách Đơn (Orders)', allow: true },
    { key: 'kitchen', icon: 'fa-fire-burner', label: '3. Màn Hình Bếp (Kitchen)', allow: isAdminOrManager || role === 'kitchen' },
    { key: 'cashier', icon: 'fa-cash-register', label: '4. Thu Ngân (Cashier)', allow: isAdminOrManager || role === 'cashier' },
    { key: 'inventory', icon: 'fa-boxes-stacked', label: '5. Quản Lý Kho & Món (Admin/Manager)', allow: isAdminOrManager },
    { key: 'users', icon: 'fa-users-gear', label: '6. Phân Quyền Nhân Viên (Admin)', allow: isAdmin }
  ].filter(t => t.allow);

  // Tự động chuyển activeTab sang tab hợp lệ đầu tiên của vai trò đó khi đăng nhập
  useEffect(() => {
    if (availableTabs.length > 0 && !availableTabs.some(t => t.key === activeTab)) {
      setActiveTab(availableTabs[0].key);
    }
  }, [currentUser]);

  const showToast = (msg, isError = false) => {
    setToast({ show: true, msg, isError });
    setTimeout(() => setToast({ show: false, msg: '', isError: false }), 3500);
  };

  // Lắng nghe sự kiện logout tự động khi Refresh Token hết hạn
  useEffect(() => {
    const handleAuthLogout = () => {
      setToken('');
      setCurrentUser(null);
      showToast('Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.', true);
    };
    window.addEventListener('auth:logout', handleAuthLogout);
    return () => window.removeEventListener('auth:logout', handleAuthLogout);
  }, []);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoginError('');
    setIsLoggingIn(true);
    try {
      const res = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: loginEmail, password: loginPassword })
      });
      const data = await res.json();
      if (!res.ok || data.error) {
        const errMsg = data.details || data.message || 'Email hoặc mật khẩu không chính xác';
        throw new Error(errMsg);
      }

      const { accessToken, refreshToken, user } = data.data;
      setTokens(accessToken, refreshToken);
      setToken(accessToken);
      setCurrentUser(user);
      localStorage.setItem('user', JSON.stringify(user));
      
      const userRole = user.role?.toLowerCase();
      if (userRole === 'kitchen') setActiveTab('kitchen');
      else if (userRole === 'cashier') setActiveTab('cashier');
      else setActiveTab('pos');

      showToast(`Xin chào ${user.full_name || user.username} (${user.role})!`);
    } catch (err) {
      let errMsg = err.message;
      if (err.message.includes('Failed to fetch') || err.message.includes('NetworkError')) {
        errMsg = 'Không thể kết nối Backend (http://localhost:3000). Vui lòng kiểm tra server Backend đã chạy chưa!';
      }
      setLoginError(errMsg);
      showToast(errMsg, true);
    } finally {
      setIsLoggingIn(false);
    }
  };

  const handleLogout = async () => {
    const refreshToken = getRefreshToken();
    if (refreshToken) {
      try {
        await fetch(`${API_BASE}/auth/logout`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ refreshToken })
        });
      } catch (err) {
        console.warn('Lỗi khi thu hồi refresh token trên server:', err.message);
      }
    }
    clearTokens();
    setToken('');
    setCurrentUser(null);
    setCart([]);
  };

  const fetchTables = async () => {
    if (!token) return;
    try {
      const res = await authFetch(`${API_BASE}/orders/tables/list`);
      const data = await res.json();
      const list = data.data || [];
      setTables(list);
      // Cập nhật selectedTable nếu đang chọn
      if (selectedTable) {
        const found = list.find(t => t.id === selectedTable.id);
        if (found) setSelectedTable(found);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleUpdateTableStatus = async (tableId, newStatus) => {
    try {
      const res = await authFetch(`${API_BASE}/orders/tables/${tableId}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.message || data.details);

      const statusMap = {
        occupied: 'Có khách ngồi (Bận)',
        available: 'Bàn trống',
        reserved: 'Đã đặt trước'
      };
      showToast(`Đã chuyển Bàn sang: ${statusMap[newStatus] || newStatus}`);
      fetchTables();
      setSelectedTable(prev => prev && prev.id === tableId ? { ...prev, status: newStatus } : prev);
    } catch (err) {
      showToast(err.message, true);
    }
  };

  const fetchProducts = async (
    page = productPage,
    limit = productLimit,
    cat = selectedCategoryFilter,
    search = productSearchKeyword
  ) => {
    if (!token) return;
    try {
      let url = `${API_BASE}/products?page=${page}&limit=${limit}`;
      if (cat && cat !== 'all') url += `&category_id=${cat}`;
      if (search && search.trim()) url += `&search=${encodeURIComponent(search.trim())}`;
      const res = await authFetch(url);
      const data = await res.json();
      setProducts(data.data || []);
      if (data.meta) {
        setProductPagination({
          total: data.meta.total,
          totalPages: data.meta.totalPages
        });
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchCategories = async () => {
    if (!token) return;
    try {
      const res = await authFetch(`${API_BASE}/categories?limit=50`);
      const data = await res.json();
      setCategories(data.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchOrders = async (
    page = orderPage,
    limit = orderLimit,
    status = orderFilterStatus,
    search = orderSearchText
  ) => {
    if (!token) return;
    try {
      let url = `${API_BASE}/orders?page=${page}&limit=${limit}`;
      if (status && status !== 'all') url += `&status=${status}`;
      if (search && search.trim()) url += `&search=${encodeURIComponent(search.trim())}`;
      const res = await authFetch(url);
      const data = await res.json();
      setOrders(data.data || []);
      if (data.meta) {
        setOrderPagination({
          total: data.meta.total,
          totalPages: data.meta.totalPages
        });
      }
      if (data.stats) {
        setOrderStats(data.stats);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchUsers = async () => {
    if (!token || !isAdmin) return;
    try {
      const res = await authFetch(`${API_BASE}/users?limit=50`);
      const data = await res.json();
      setUsersList(data.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (token) {
      fetchTables();
      fetchProducts();
      fetchOrders();
      fetchCategories();
      if (isAdmin) {
        fetchUsers();
      }
    }
  }, [token, role]);

  useEffect(() => {
    if (token && (activeTab === 'kitchen' || activeTab === 'cashier' || activeTab === 'orders' || activeTab === 'pos')) {
      fetchOrders();
    }
    if (token && activeTab === 'inventory') {
      fetchProducts();
      fetchCategories();
    }
    if (token && activeTab === 'users' && isAdmin) {
      fetchUsers();
    }
  }, [activeTab]);

  // Update Role API (Admin Only)
  const handleUpdateRole = async (userId, newRole) => {
    try {
      const res = await authFetch(`${API_BASE}/users/${userId}/role`, {
        method: 'PATCH',
        body: JSON.stringify({ role: newRole })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.message || data.details);

      showToast(`✅ Đã cập nhật vai trò sang: ${newRole.toUpperCase()}`);
      fetchUsers();
    } catch (err) {
      showToast(err.message, true);
    }
  };

  // Cập nhật tồn kho (Quick Stock Update - Admin/Manager)
  const handleUpdateStock = async (productId, newQuantity) => {
    const qty = parseInt(newQuantity);
    if (isNaN(qty) || qty < 0) {
      showToast('Số lượng tồn kho không hợp lệ', true);
      return;
    }
    try {
      const res = await authFetch(`${API_BASE}/products/${productId}`, {
        method: 'PUT',
        body: JSON.stringify({
          quantity: qty,
          status: qty > 0 ? 'available' : 'unavailable'
        })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.message || data.details);

      showToast('✅ Đã cập nhật tồn kho & xóa cache Redis');
      fetchProducts();
    } catch (err) {
      showToast(err.message, true);
    }
  };

  // Toggle trạng thái mở bán món (available / unavailable)
  const handleToggleProductStatus = async (product) => {
    const newStatus = product.status === 'available' ? 'unavailable' : 'available';
    try {
      const res = await authFetch(`${API_BASE}/products/${product.id}`, {
        method: 'PUT',
        body: JSON.stringify({ status: newStatus })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.message || data.details);

      showToast(`Đã chuyển món sang: ${newStatus}`);
      fetchProducts();
    } catch (err) {
      showToast(err.message, true);
    }
  };

  // Tạo món ăn mới (Create Product - Admin/Manager)
  const handleCreateProduct = async (e) => {
    e.preventDefault();
    if (!newProductData.name || !newProductData.price || !newProductData.category_id) {
      showToast('Vui lòng điền đủ tên, giá và danh mục', true);
      return;
    }
    try {
      const res = await authFetch(`${API_BASE}/products`, {
        method: 'POST',
        body: JSON.stringify({
          name: newProductData.name,
          price: parseFloat(newProductData.price),
          quantity: parseInt(newProductData.quantity) || 0,
          category_id: newProductData.category_id,
          description: newProductData.description
        })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.message || data.details);

      showToast('🎉 Thêm món ăn mới thành công!');
      setShowAddProductModal(false);
      setNewProductData({ name: '', price: '', quantity: 20, category_id: '', description: '' });
      fetchProducts();
    } catch (err) {
      showToast(err.message, true);
    }
  };

  // Cart Logic
  const addToCart = (product, count = 1) => {
    const existing = cart.find((x) => x.product.id === product.id);
    const currentQty = existing ? existing.quantity : 0;
    const targetQty = currentQty + count;
    if (targetQty > product.quantity) {
      showToast(`Kho chỉ còn ${product.quantity} phần! (Trong đơn đã có ${currentQty})`, true);
      return false;
    }
    if (existing) {
      setCart(
        cart.map((x) =>
          x.product.id === product.id
            ? { ...x, quantity: targetQty }
            : x
        )
      );
    } else {
      setCart([...cart, { product, quantity: count }]);
    }
    return true;
  };

  const updateCartQuantity = (productId, delta) => {
    setCart((prev) =>
      prev
        .map((item) => {
          if (item.product.id === productId) {
            return { ...item, quantity: item.quantity + delta };
          }
          return item;
        })
        .filter((item) => item.quantity > 0)
    );
  };

  const cartTotal = cart.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  // Submit Order
  const handleCreateOrder = async () => {
    if (cart.length === 0) {
      showToast('Vui lòng chọn ít nhất 1 món ăn!', true);
      return;
    }
    try {
      const res = await authFetch(`${API_BASE}/orders`, {
        method: 'POST',
        body: JSON.stringify({
          table_id: selectedTable ? selectedTable.id : null,
          items: cart.map((x) => ({
            product_id: x.product.id,
            quantity: x.quantity
          })),
          customer_email: customerEmail ? customerEmail.trim() : null,
          note: customerEmail ? `Khách: ${customerEmail.trim()}` : null
        })
      });
      const data = await res.json();
      if (data.error) {
        if (data.details && Array.isArray(data.details)) {
          throw new Error(data.details.map((d) => d.message).join('; '));
        }
        throw new Error(data.message || data.details);
      }
      showToast(
        `✅ Tạo đơn #${data.data.id.substring(0, 8)} thành công! Đã gửi vào Bếp.`
      );
      setCart([]);
      setSelectedTable(null);
      fetchTables();
      fetchProducts();
      fetchOrders();
    } catch (err) {
      showToast(err.message, true);
    }
  };

  // Kitchen Status Change
  const handleChangeStatus = async (orderID, new_status) => {
    try {
      const res = await authFetch(`${API_BASE}/orders/status`, {
        method: 'PATCH',
        body: JSON.stringify({ orderID, new_status })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.message || data.details);
      showToast(`Đã chuyển đơn sang: ${new_status.toUpperCase()}`);
      fetchOrders();
      if (new_status === 'cancelled') {
        fetchProducts(); // Hủy đơn thì hoàn kho nên tải lại sản phẩm
      }
    } catch (err) {
      showToast(err.message, true);
    }
  };

  // Cashier Payment
  const handlePayment = async (orderId) => {
    if (!window.confirm('Xác nhận khách đã thanh toán hóa đơn này?')) return;
    try {
      const res = await authFetch(`${API_BASE}/payments`, {
        method: 'POST',
        body: JSON.stringify({
          order_id: orderId,
          payment_method: 'banking'
        })
      });
      const data = await res.json();
      if (data.error) throw new Error(data.message || data.details);

      showToast(
        '🎉 Thanh toán thành công! Đã giải phóng bàn & kích hoạt Kafka gửi email.'
      );
      fetchOrders();
      fetchTables();
    } catch (err) {
      showToast(err.message, true);
    }
  };

  // Render Login View
  if (!token) {
    const demoAccounts = [
      { role: 'Admin', email: 'admin@restaurant.com', badge: 'bg-red-100 text-red-700' },
      { role: 'Quản Lý', email: 'manager01@restaurant.com', badge: 'bg-purple-100 text-purple-700' },
      { role: 'Phục Vụ', email: 'waiter01@restaurant.com', badge: 'bg-blue-100 text-blue-700' },
      { role: 'Bếp', email: 'kitchen01@restaurant.com', badge: 'bg-orange-100 text-orange-700' },
      { role: 'Thu Ngân', email: 'cashier01@restaurant.com', badge: 'bg-emerald-100 text-emerald-700' },
    ];

    return (
      <div className="min-h-screen flex items-center justify-center p-4 bg-slate-50">
        <div className="max-w-md w-full bg-white p-8 rounded-2xl shadow-xl border border-slate-100">
          <div className="text-center mb-6">
            <div className="inline-flex p-3 bg-emerald-100 text-emerald-600 rounded-2xl mb-3">
              <i className="fa-solid fa-utensils text-3xl"></i>
            </div>
            <h2 className="text-2xl font-bold text-slate-800">
              Đăng Nhập React POS
            </h2>
            <p className="text-xs text-slate-400 mt-1">
              Hệ thống Quản trị & Điều phối Nhà hàng Toàn diện
            </p>
          </div>

          {/* Cảnh báo lỗi nếu có */}
          {loginError && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-xl text-xs flex items-start space-x-2">
              <i className="fa-solid fa-circle-exclamation mt-0.5 text-sm shrink-0"></i>
              <div className="flex-1 font-medium">{loginError}</div>
            </div>
          )}

          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <label className="block text-xs font-bold text-slate-600 mb-1">
                Email hoặc Tên đăng nhập
              </label>
              <input
                type="text"
                required
                value={loginEmail}
                onChange={(e) => setLoginEmail(e.target.value)}
                placeholder="admin@restaurant.com hoặc admin01"
                className="w-full px-4 py-2 text-sm border rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-600 mb-1">
                Mật khẩu
              </label>
              <input
                type="password"
                required
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
                placeholder="Password123@"
                className="w-full px-4 py-2 text-sm border rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>
            <button
              type="submit"
              disabled={isLoggingIn}
              className={`w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 rounded-xl shadow-lg shadow-emerald-200 transition flex items-center justify-center space-x-2 ${
                isLoggingIn ? 'opacity-70 cursor-not-allowed' : ''
              }`}
            >
              {isLoggingIn ? (
                <>
                  <i className="fa-solid fa-circle-notch fa-spin"></i>
                  <span>Đang Đăng Nhập...</span>
                </>
              ) : (
                <span>Đăng Nhập</span>
              )}
            </button>
          </form>

          {/* Quick Fill Demo Accounts */}
          <div className="mt-6 pt-5 border-t border-slate-100">
            <p className="text-xs font-bold text-slate-500 mb-2.5 flex items-center">
              <i className="fa-solid fa-bolt mr-1.5 text-amber-500"></i>
              Chọn tài khoản mẫu để test phân quyền:
            </p>
            <div className="flex flex-wrap gap-1.5">
              {demoAccounts.map((acc) => (
                <button
                  key={acc.email}
                  type="button"
                  onClick={() => {
                    setLoginEmail(acc.email);
                    setLoginPassword('Password123@');
                    setLoginError('');
                  }}
                  className={`text-xs px-2.5 py-1 rounded-lg font-medium border hover:opacity-80 transition flex items-center space-x-1 ${acc.badge}`}
                >
                  <span>{acc.role}</span>
                </button>
              ))}
            </div>
            <p className="text-[11px] text-slate-400 mt-2 italic">
              * Mật khẩu mặc định: <code className="bg-slate-100 px-1 py-0.5 rounded text-slate-700">Password123@</code>
            </p>
          </div>
        </div>

        {/* Toast Notification khi ở màn hình Login */}
        {toast.show && (
          <div
            className={`fixed bottom-5 right-5 z-50 px-5 py-3 rounded-xl shadow-xl flex items-center space-x-3 transition-all ${
              toast.isError ? 'bg-red-600 text-white' : 'bg-slate-900 text-white'
            }`}
          >
            <i
              className={`fa-solid ${
                toast.isError
                  ? 'fa-triangle-exclamation'
                  : 'fa-circle-check text-emerald-400'
              } text-lg`}
            ></i>
            <span className="text-xs font-medium">{toast.msg}</span>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen">
      {/* Header */}
      <header className="bg-emerald-600 text-white shadow-md sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 py-3 flex justify-between items-center">
          <div className="flex items-center space-x-3">
            <span className="bg-emerald-700 p-2 rounded-xl">
              <i className="fa-brands fa-react text-xl text-emerald-300"></i>
            </span>
            <h1 className="text-lg font-bold tracking-wide">
              MTD Restaurant POS & ERP
            </h1>
          </div>
          <div className="flex items-center space-x-3">
            <span className="bg-emerald-700/80 px-3 py-1 rounded-full text-xs font-semibold">
              <i className="fa-solid fa-user-tag mr-1"></i>
              {currentUser?.role?.toUpperCase()}:{' '}
              {currentUser?.full_name || currentUser?.username}
            </span>
            <button
              onClick={handleLogout}
              className="text-xs text-emerald-100 hover:text-white underline"
            >
              Đăng xuất
            </button>
          </div>
        </div>
      </header>

      {/* Main Workspace */}
      <main className="flex-1 max-w-7xl mx-auto w-full p-4 md:p-6 space-y-6">
        {/* Dynamic Navigation Tabs based on Role */}
        <div className="flex flex-wrap gap-2 border-b border-slate-200 pb-1">
          {availableTabs.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`px-4 py-2 font-bold text-sm flex items-center space-x-2 rounded-t-xl transition ${
                activeTab === tab.key
                  ? 'border-b-2 border-emerald-600 text-emerald-600 bg-white shadow-sm'
                  : 'text-slate-500 hover:text-emerald-600'
              }`}
            >
              <i className={`fa-solid ${tab.icon}`}></i>
              <span>{tab.label}</span>
            </button>
          ))}
        </div>

        {/* TAB 1: POS - SƠ ĐỒ BÀN & GỌI MÓN (Waiter, Cashier, Admin, Manager) */}
        {activeTab === 'pos' && (isAdminOrManager || role === 'waiter' || role === 'cashier') && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 space-y-6">
              {/* Tables Grid & Lễ tân đón khách */}
              <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-100 space-y-4">
                <div className="flex flex-wrap justify-between items-center gap-2">
                  <div>
                    <h3 className="font-bold text-slate-800 text-sm flex items-center">
                      <i className="fa-solid fa-chair text-emerald-600 mr-2"></i>
                      Sơ Đồ Bàn Ăn & Điều Phối Lễ Tân
                    </h3>
                    <p className="text-[11px] text-slate-400 mt-0.5">
                      Lễ tân / Phục vụ có thể bấm chọn bàn để xếp khách (Set Bận) ngay khi khách vào
                    </p>
                  </div>
                  {/* Chú thích màu sắc */}
                  <div className="flex items-center space-x-3 text-xs">
                    <span className="flex items-center text-slate-600">
                      <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 mr-1.5"></span>
                      Trống
                    </span>
                    <span className="flex items-center text-slate-600">
                      <span className="w-2.5 h-2.5 rounded-full bg-rose-500 mr-1.5"></span>
                      Đang có khách
                    </span>
                    <span className="flex items-center text-slate-600">
                      <span className="w-2.5 h-2.5 rounded-full bg-amber-500 mr-1.5"></span>
                      Đặt trước
                    </span>
                  </div>
                </div>

                {/* Grid các bàn */}
                <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-6 gap-3">
                  {tables.map((t) => {
                    const isSel = selectedTable?.id === t.id;
                    const isOcc = t.status === 'occupied';
                    const isRes = t.status === 'reserved';

                    let cardClass = 'bg-emerald-50/50 text-emerald-900 border-emerald-200 hover:border-emerald-400';
                    let badgeClass = 'bg-emerald-100 text-emerald-700';
                    let statusLabel = 'Trống';

                    if (isOcc) {
                      cardClass = 'bg-rose-50/60 text-rose-900 border-rose-200 hover:border-rose-300';
                      badgeClass = 'bg-rose-100 text-rose-700 font-bold';
                      statusLabel = 'Có khách';
                    } else if (isRes) {
                      cardClass = 'bg-amber-50/60 text-amber-900 border-amber-200 hover:border-amber-300';
                      badgeClass = 'bg-amber-100 text-amber-700 font-bold';
                      statusLabel = 'Đặt trước';
                    }

                    if (isSel) {
                      cardClass = 'ring-2 ring-emerald-600 bg-white border-emerald-600 shadow-md shadow-emerald-100';
                    }

                    return (
                      <button
                        key={t.id}
                        type="button"
                        onClick={() => setSelectedTable(t)}
                        className={`p-3 rounded-xl border text-center transition flex flex-col items-center justify-between relative cursor-pointer ${cardClass}`}
                      >
                        <div className="w-full flex justify-between items-center text-[10px] text-slate-400 mb-1">
                          <span className="font-semibold text-slate-500">{t.capacity} chỗ</span>
                          <span className={`px-1.5 py-0.5 rounded-md text-[9px] ${badgeClass}`}>
                            {statusLabel}
                          </span>
                        </div>
                        <i className={`fa-solid fa-chair text-xl my-1 ${isOcc ? 'text-rose-500' : isRes ? 'text-amber-500' : 'text-emerald-600'}`}></i>
                        <span className="font-bold text-xs text-slate-800">
                          Bàn {t.table_number}
                        </span>
                      </button>
                    );
                  })}
                </div>

                {/* Thanh tác vụ nhanh cho bàn đang chọn (Set bận / Set trống) */}
                {selectedTable && (
                  <div className="mt-3 p-3.5 bg-slate-50 rounded-xl border border-slate-200 flex flex-wrap items-center justify-between gap-3">
                    <div className="flex items-center space-x-2">
                      <div className="w-8 h-8 rounded-lg bg-emerald-600 text-white flex items-center justify-center font-bold text-xs">
                        {selectedTable.table_number}
                      </div>
                      <div>
                        <div className="text-xs font-bold text-slate-800 flex items-center space-x-1.5">
                          <span>Bàn {selectedTable.table_number} ({selectedTable.capacity} chỗ)</span>
                          <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold ${
                            selectedTable.status === 'occupied' 
                              ? 'bg-rose-100 text-rose-700' 
                              : selectedTable.status === 'reserved'
                              ? 'bg-amber-100 text-amber-700'
                              : 'bg-emerald-100 text-emerald-700'
                          }`}>
                            {selectedTable.status === 'occupied' ? '🔴 Đang có khách ngồi' : selectedTable.status === 'reserved' ? '🟡 Đã đặt trước' : '🟢 Bàn đang trống'}
                          </span>
                        </div>
                        <p className="text-[11px] text-slate-400">
                          {selectedTable.status === 'occupied' 
                            ? 'Khách đang ngồi tại bàn. Bạn có thể chọn món bên dưới để lên đơn.'
                            : 'Bàn sẵn sàng đón khách mới.'}
                        </p>
                      </div>
                    </div>

                    {/* Các nút bấm set trạng thái tức thì cho Lễ tân / Waiter */}
                    <div className="flex items-center space-x-2">
                      {selectedTable.status === 'available' && (
                        <>
                          <button
                            type="button"
                            onClick={() => handleUpdateTableStatus(selectedTable.id, 'occupied')}
                            className="px-3 py-1.5 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-lg shadow-sm transition flex items-center space-x-1"
                          >
                            <i className="fa-solid fa-user-plus mr-1"></i>
                            <span>Xếp Khách Vào Bàn (Set Bận)</span>
                          </button>
                          <button
                            type="button"
                            onClick={() => handleUpdateTableStatus(selectedTable.id, 'reserved')}
                            className="px-2.5 py-1.5 bg-amber-500 hover:bg-amber-600 text-white text-xs font-medium rounded-lg shadow-sm transition"
                          >
                            <span>Đặt trước</span>
                          </button>
                        </>
                      )}

                      {selectedTable.status === 'occupied' && (
                        <>
                          {(() => {
                            const activeOrder = orders.find(
                              (o) => o.table_id === selectedTable.id && o.status !== 'completed' && o.status !== 'cancelled'
                            );
                            if (activeOrder) {
                              return (
                                <button
                                  type="button"
                                  onClick={() => setSelectedOrderDetail(activeOrder)}
                                  className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-lg shadow-sm transition flex items-center space-x-1"
                                >
                                  <i className="fa-solid fa-receipt mr-1"></i>
                                  <span>Xem Đơn Bàn Đang Ăn ({activeOrder.items?.length || 0} món)</span>
                                </button>
                              );
                            }
                            return null;
                          })()}
                          <button
                            type="button"
                            onClick={() => handleUpdateTableStatus(selectedTable.id, 'available')}
                            className="px-3 py-1.5 bg-slate-600 hover:bg-slate-700 text-white text-xs font-bold rounded-lg shadow-sm transition flex items-center space-x-1"
                          >
                            <i className="fa-solid fa-arrow-rotate-left mr-1"></i>
                            <span>Khách rời bàn / Trả bàn trống</span>
                          </button>
                        </>
                      )}

                      {selectedTable.status === 'reserved' && (
                        <>
                          <button
                            type="button"
                            onClick={() => handleUpdateTableStatus(selectedTable.id, 'occupied')}
                            className="px-3 py-1.5 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-lg shadow-sm transition"
                          >
                            <i className="fa-solid fa-check mr-1"></i>
                            <span>Khách đến (Set Có Khách)</span>
                          </button>
                          <button
                            type="button"
                            onClick={() => handleUpdateTableStatus(selectedTable.id, 'available')}
                            className="px-2.5 py-1.5 bg-slate-500 hover:bg-slate-600 text-white text-xs font-medium rounded-lg shadow-sm transition"
                          >
                            <span>Hủy đặt</span>
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                )}
              </div>

              {/* Products Grid & Pagination */}
              <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-100 space-y-4">
                <div className="flex flex-wrap justify-between items-center gap-3 pb-2 border-b border-slate-100">
                  <div>
                    <h3 className="font-bold text-slate-800 text-sm flex items-center">
                      <i className="fa-solid fa-bowl-food text-emerald-600 mr-2"></i>
                      Thực Đơn Món Ăn
                    </h3>
                    <p className="text-[11px] text-slate-400 mt-0.5">
                      Nhấn vào món ăn để xem ảnh lớn, mô tả chi tiết & chọn số lượng
                    </p>
                  </div>

                  {/* Thanh tìm kiếm món */}
                  <div className="flex items-center space-x-2">
                    <div className="relative w-48 sm:w-56">
                      <i className="fa-solid fa-magnifying-glass absolute left-3 top-2.5 text-slate-400 text-xs"></i>
                      <input
                        type="text"
                        value={productSearchKeyword}
                        onChange={(e) => {
                          const val = e.target.value;
                          setProductSearchKeyword(val);
                          setProductPage(1);
                          fetchProducts(1, productLimit, selectedCategoryFilter, val);
                        }}
                        placeholder="Tìm kiếm món ăn..."
                        className="w-full pl-8 pr-3 py-1.5 border rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                      />
                      {productSearchKeyword && (
                        <button
                          type="button"
                          onClick={() => {
                            setProductSearchKeyword('');
                            setProductPage(1);
                            fetchProducts(1, productLimit, selectedCategoryFilter, '');
                          }}
                          className="absolute right-2.5 top-2 text-slate-400 hover:text-slate-600 text-xs"
                        >
                          <i className="fa-solid fa-xmark"></i>
                        </button>
                      )}
                    </div>
                  </div>
                </div>

                {/* Bộ lọc Danh mục món ăn (Category Filter Pills) */}
                <div className="flex flex-wrap items-center gap-1.5">
                  <button
                    type="button"
                    onClick={() => {
                      setSelectedCategoryFilter('all');
                      setProductPage(1);
                      fetchProducts(1, productLimit, 'all', productSearchKeyword);
                    }}
                    className={`px-3 py-1 rounded-lg text-xs font-bold transition ${
                      selectedCategoryFilter === 'all'
                        ? 'bg-emerald-600 text-white shadow-sm'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    Tất cả danh mục
                  </button>
                  {categories.map((c) => (
                    <button
                      key={c.id}
                      type="button"
                      onClick={() => {
                        setSelectedCategoryFilter(c.id);
                        setProductPage(1);
                        fetchProducts(1, productLimit, c.id, productSearchKeyword);
                      }}
                      className={`px-3 py-1 rounded-lg text-xs font-bold transition ${
                        selectedCategoryFilter === c.id
                          ? 'bg-emerald-600 text-white shadow-sm'
                          : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                      }`}
                    >
                      {c.name}
                    </button>
                  ))}
                </div>

                {/* Danh sách món ăn Grid */}
                {products.length === 0 ? (
                  <div className="py-12 text-center text-slate-400 text-xs">
                    <i className="fa-solid fa-utensils text-3xl mb-2 text-slate-300 block"></i>
                    Không tìm thấy món ăn nào phù hợp với bộ lọc
                  </div>
                ) : (
                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                    {products.map((p) => {
                      const isOutOfStock = p.quantity <= 0 || p.status !== 'available';
                      const imgUrl = getProductImage(p);

                      return (
                        <div
                          key={p.id}
                          className="group border border-slate-100 rounded-2xl bg-white hover:shadow-lg hover:border-emerald-200 transition-all duration-200 flex flex-col justify-between overflow-hidden"
                        >
                          {/* Hình ảnh món ăn & Nút xem chi tiết */}
                          <div
                            onClick={() => {
                              setSelectedProductDetail(p);
                              setDetailQuantity(1);
                            }}
                            className="relative h-40 w-full overflow-hidden bg-slate-100 cursor-pointer"
                          >
                            <img
                              src={imgUrl}
                              alt={p.name}
                              className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                              loading="lazy"
                            />
                            {/* Lớp phủ mờ & biểu tượng xem chi tiết khi hover */}
                            <div className="absolute inset-0 bg-black/25 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                              <span className="bg-white/95 text-slate-800 text-xs font-bold px-3 py-1.5 rounded-full shadow-md flex items-center space-x-1.5">
                                <i className="fa-solid fa-eye text-emerald-600 text-xs"></i>
                                <span>Xem chi tiết</span>
                              </span>
                            </div>

                            {/* Badge Tồn kho */}
                            <span
                              className={`absolute top-2 right-2 text-[10px] px-2.5 py-0.5 rounded-full font-bold shadow-sm ${
                                !isOutOfStock
                                  ? 'bg-emerald-500/90 text-white'
                                  : 'bg-rose-500/90 text-white'
                              }`}
                            >
                              {!isOutOfStock ? `Còn: ${p.quantity}` : 'Hết hàng'}
                            </span>
                          </div>

                          {/* Thông tin món */}
                          <div className="p-3.5 flex flex-col flex-1 justify-between">
                            <div
                              onClick={() => {
                                setSelectedProductDetail(p);
                                setDetailQuantity(1);
                              }}
                              className="cursor-pointer"
                            >
                              <h4 className="font-bold text-slate-800 text-sm group-hover:text-emerald-600 transition-colors line-clamp-1">
                                {p.name}
                              </h4>
                              <p className="text-xs text-slate-400 mt-1 line-clamp-2 leading-relaxed">
                                {p.description || 'Hương vị thơm ngon được tuyển chọn từ nguyên liệu tươi mới mỗi ngày.'}
                              </p>
                            </div>

                            {/* Giá tiền & Nút thêm nhanh */}
                            <div className="flex justify-between items-center mt-3 pt-2.5 border-t border-slate-50">
                              <span className="font-extrabold text-emerald-600 text-sm">
                                {p.price.toLocaleString('vi-VN')} đ
                              </span>
                              <button
                                disabled={isOutOfStock}
                                onClick={(e) => {
                                  e.stopPropagation();
                                  const success = addToCart(p, 1);
                                  if (success) {
                                    showToast(`+1 ${p.name} vào đơn`);
                                  }
                                }}
                                className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-200 disabled:text-slate-400 text-white text-xs font-bold rounded-xl shadow-sm transition flex items-center space-x-1 cursor-pointer"
                              >
                                <i className="fa-solid fa-plus text-[10px]"></i>
                                <span>Thêm</span>
                              </button>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}

                {/* THANH PHÂN TRANG (Pagination Bar) */}
                <div className="pt-3 border-t border-slate-100 flex flex-wrap items-center justify-between gap-3 text-xs">
                  <div className="text-slate-500 flex items-center space-x-1">
                    <span>Trang <b className="text-slate-800">{productPage}</b> / {productPagination.totalPages || 1}</span>
                    <span className="text-slate-300 mx-1">•</span>
                    <span>Tổng cộng: <b className="text-slate-800">{productPagination.total}</b> món</span>
                  </div>

                  {/* Nút chuyển trang */}
                  <div className="flex items-center space-x-1.5">
                    {/* Nút Trước */}
                    <button
                      type="button"
                      disabled={productPage <= 1}
                      onClick={() => {
                        const newP = Math.max(1, productPage - 1);
                        setProductPage(newP);
                        fetchProducts(newP, productLimit, selectedCategoryFilter, productSearchKeyword);
                      }}
                      className="px-2.5 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-100 disabled:opacity-40 disabled:hover:bg-transparent font-medium flex items-center space-x-1 transition cursor-pointer"
                    >
                      <i className="fa-solid fa-chevron-left text-[10px]"></i>
                      <span className="hidden sm:inline">Trước</span>
                    </button>

                    {/* Dãy nút số trang */}
                    {Array.from({ length: productPagination.totalPages || 1 }, (_, i) => i + 1).map((pageNum) => (
                      <button
                        key={pageNum}
                        type="button"
                        onClick={() => {
                          setProductPage(pageNum);
                          fetchProducts(pageNum, productLimit, selectedCategoryFilter, productSearchKeyword);
                        }}
                        className={`w-7 h-7 rounded-lg font-bold transition flex items-center justify-center cursor-pointer ${
                          productPage === pageNum
                            ? 'bg-emerald-600 text-white shadow-sm'
                            : 'hover:bg-slate-100 text-slate-700'
                        }`}
                      >
                        {pageNum}
                      </button>
                    ))}

                    {/* Nút Sau */}
                    <button
                      type="button"
                      disabled={productPage >= productPagination.totalPages}
                      onClick={() => {
                        const newP = Math.min(productPagination.totalPages, productPage + 1);
                        setProductPage(newP);
                        fetchProducts(newP, productLimit, selectedCategoryFilter, productSearchKeyword);
                      }}
                      className="px-2.5 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-100 disabled:opacity-40 disabled:hover:bg-transparent font-medium flex items-center space-x-1 transition cursor-pointer"
                    >
                      <span className="hidden sm:inline">Sau</span>
                      <i className="fa-solid fa-chevron-right text-[10px]"></i>
                    </button>
                  </div>

                  {/* Tùy chọn số món trên 1 trang */}
                  <div className="flex items-center space-x-1.5 text-slate-500">
                    <span className="text-[11px]">Hiển thị:</span>
                    {[6, 9, 12].map((sz) => (
                      <button
                        key={sz}
                        type="button"
                        onClick={() => {
                          setProductLimit(sz);
                          setProductPage(1);
                          fetchProducts(1, sz, selectedCategoryFilter, productSearchKeyword);
                        }}
                        className={`px-2 py-0.5 rounded text-[11px] font-bold transition cursor-pointer ${
                          productLimit === sz
                            ? 'bg-slate-800 text-white'
                            : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                        }`}
                      >
                        {sz}
                      </button>
                    ))}
                    <span className="text-[11px]">món</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Cart Sidebar */}
            <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-100 flex flex-col h-fit sticky top-20">
              <div className="flex justify-between items-center pb-3 border-b border-slate-100">
                <h3 className="font-bold text-slate-800 text-sm">
                  Đơn Hàng (Cart)
                </h3>
                <span className="text-xs px-2.5 py-1 rounded-full font-bold bg-emerald-50 text-emerald-700">
                  {selectedTable
                    ? `Bàn ${selectedTable.table_number}`
                    : 'Chưa chọn bàn'}
                </span>
              </div>

              <div className="my-3">
                <label className="block text-xs font-semibold text-slate-500 mb-1">
                  Email nhận hóa đơn (Khách):
                </label>
                <input
                  type="email"
                  value={customerEmail}
                  onChange={(e) => setCustomerEmail(e.target.value)}
                  className="w-full text-xs px-3 py-2 border rounded-lg focus:outline-none focus:ring-1 focus:ring-emerald-500"
                  placeholder="email@domain.com"
                />
              </div>

              <div className="flex-1 overflow-y-auto max-h-72 space-y-2 divide-y divide-slate-100">
                {cart.length === 0 ? (
                  <p className="text-xs text-slate-400 text-center py-8">
                    Chưa có món nào được chọn
                  </p>
                ) : (
                  cart.map((item) => (
                    <div
                      key={item.product.id}
                      className="py-2 flex justify-between items-center text-xs"
                    >
                      <div className="flex-1 pr-2">
                        <p className="font-bold text-slate-800">
                          {item.product.name}
                        </p>
                        <p className="text-slate-400">
                          {item.product.price.toLocaleString('vi-VN')} đ
                        </p>
                      </div>
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => updateCartQuantity(item.product.id, -1)}
                          className="w-5 h-5 bg-slate-100 rounded text-slate-600 font-bold hover:bg-slate-200"
                        >
                          -
                        </button>
                        <span className="font-bold">{item.quantity}</span>
                        <button
                          onClick={() => updateCartQuantity(item.product.id, 1)}
                          className="w-5 h-5 bg-slate-100 rounded text-slate-600 font-bold hover:bg-slate-200"
                        >
                          +
                        </button>
                      </div>
                      <div className="w-16 text-right font-bold text-slate-700">
                        {(item.product.price * item.quantity).toLocaleString(
                          'vi-VN'
                        )}{' '}
                        đ
                      </div>
                    </div>
                  ))
                )}
              </div>

              <div className="border-t border-slate-100 pt-4 mt-3 space-y-3">
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-500 font-medium">Tổng tiền:</span>
                  <span className="text-lg font-bold text-emerald-600">
                    {cartTotal.toLocaleString('vi-VN')} đ
                  </span>
                </div>
                <button
                  onClick={handleCreateOrder}
                  className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 rounded-xl shadow-lg shadow-emerald-100 transition flex items-center justify-center space-x-2 text-sm"
                >
                  <i className="fa-solid fa-paper-plane text-xs"></i>
                  <span>Gửi Đơn Vào Bếp</span>
                </button>
              </div>
            </div>
          </div>
        )}

        {/* TAB 2: QUẢN LÝ & TRA CỨU ĐƠN HÀNG (Backend Pagination & Filter) */}
        {activeTab === 'orders' && (
          <div className="space-y-6">
            {/* Thanh Thống kê nhanh từ Backend */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              <div className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex items-center space-x-3">
                <div className="w-10 h-10 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center text-lg">
                  <i className="fa-solid fa-receipt"></i>
                </div>
                <div>
                  <span className="text-[11px] text-slate-400 font-semibold block">Tổng Đơn Hàng</span>
                  <span className="text-lg font-black text-slate-800">{orderStats.totalOrders} đơn</span>
                </div>
              </div>

              <div className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex items-center space-x-3">
                <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center text-lg">
                  <i className="fa-solid fa-fire-burner"></i>
                </div>
                <div>
                  <span className="text-[11px] text-slate-400 font-semibold block">Đang Phục Vụ</span>
                  <span className="text-lg font-black text-amber-600">
                    {orderStats.activeCount} đơn
                  </span>
                </div>
              </div>

              <div className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex items-center space-x-3">
                <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center text-lg">
                  <i className="fa-solid fa-circle-check"></i>
                </div>
                <div>
                  <span className="text-[11px] text-slate-400 font-semibold block">Đã Hoàn Tất</span>
                  <span className="text-lg font-black text-emerald-600">
                    {orderStats.completedCount} đơn
                  </span>
                </div>
              </div>

              <div className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex items-center space-x-3">
                <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center text-lg">
                  <i className="fa-solid fa-money-bill-wave"></i>
                </div>
                <div>
                  <span className="text-[11px] text-slate-400 font-semibold block">Doanh Thu Đơn</span>
                  <span className="text-base font-black text-purple-700">
                    {orderStats.totalRevenue.toLocaleString('vi-VN')} đ
                  </span>
                </div>
              </div>
            </div>

            {/* Thanh Tìm Kiếm & Lọc Trạng Thái Backend */}
            <div className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm space-y-3">
              <div className="flex flex-wrap items-center justify-between gap-3">
                {/* Search Box - Gửi truy vấn trực tiếp lên BE */}
                <div className="relative flex-1 min-w-[260px]">
                  <i className="fa-solid fa-magnifying-glass absolute left-3.5 top-3 text-slate-400 text-xs"></i>
                  <input
                    type="text"
                    value={orderSearchText}
                    onChange={(e) => {
                      const val = e.target.value;
                      setOrderSearchText(val);
                      setOrderPage(1);
                      fetchOrders(1, orderLimit, orderFilterStatus, val);
                    }}
                    placeholder="Tìm theo Số bàn (ví dụ: 1), Mã đơn (#...), hoặc Người tạo..."
                    className="w-full pl-9 pr-4 py-2 border rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                  {orderSearchText && (
                    <button
                      type="button"
                      onClick={() => {
                        setOrderSearchText('');
                        setOrderPage(1);
                        fetchOrders(1, orderLimit, orderFilterStatus, '');
                      }}
                      className="absolute right-3 top-2.5 text-slate-400 hover:text-slate-600 text-xs"
                    >
                      <i className="fa-solid fa-xmark"></i>
                    </button>
                  )}
                </div>

                <button
                  type="button"
                  onClick={() => fetchOrders(orderPage, orderLimit, orderFilterStatus, orderSearchText)}
                  className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition flex items-center space-x-1.5 cursor-pointer"
                >
                  <i className="fa-solid fa-rotate-right"></i>
                  <span>Làm mới danh sách</span>
                </button>
              </div>

              {/* Status Filter Pills - Lọc bằng Backend Query */}
              <div className="flex flex-wrap items-center gap-1.5 pt-1">
                {[
                  { key: 'all', label: 'Tất cả đơn' },
                  { key: 'pending', label: 'Chờ nhận (Pending)' },
                  { key: 'cooking', label: 'Đang nấu (Cooking)' },
                  { key: 'ready', label: 'Món đã xong (Ready)' },
                  { key: 'served', label: 'Đã lên bàn (Served)' },
                  { key: 'completed', label: 'Đã thanh toán (Completed)' },
                  { key: 'cancelled', label: 'Đã hủy (Cancelled)' }
                ].map(f => (
                  <button
                    key={f.key}
                    type="button"
                    onClick={() => {
                      setOrderFilterStatus(f.key);
                      setOrderPage(1);
                      fetchOrders(1, orderLimit, f.key, orderSearchText);
                    }}
                    className={`px-3 py-1.5 rounded-lg text-xs font-bold transition cursor-pointer ${
                      orderFilterStatus === f.key
                        ? 'bg-emerald-600 text-white shadow-sm'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    {f.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Bảng Danh Sách Đơn Hàng Phân Trang */}
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs text-slate-600">
                  <thead className="bg-slate-50 text-slate-700 uppercase font-bold border-b border-slate-100">
                    <tr>
                      <th className="px-5 py-3">Mã Đơn</th>
                      <th className="px-5 py-3">Bàn Ăn</th>
                      <th className="px-5 py-3">Thời Gian Đặt</th>
                      <th className="px-5 py-3">Số Lượng Món</th>
                      <th className="px-5 py-3">Tổng Tiền</th>
                      <th className="px-5 py-3">Người Tạo Đơn</th>
                      <th className="px-5 py-3">Trạng Thái</th>
                      <th className="px-5 py-3 text-right">Chi Tiết</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {orders.length === 0 ? (
                      <tr>
                        <td colSpan="8" className="px-5 py-12 text-center text-slate-400 text-xs">
                          <i className="fa-solid fa-receipt text-3xl mb-2 text-slate-300 block"></i>
                          Không có đơn hàng nào khớp với điều kiện lọc
                        </td>
                      </tr>
                    ) : (
                      orders.map((o) => {
                        const statusColors = {
                          pending: 'bg-amber-100 text-amber-800',
                          cooking: 'bg-orange-100 text-orange-800',
                          ready: 'bg-blue-100 text-blue-800',
                          served: 'bg-indigo-100 text-indigo-800',
                          completed: 'bg-emerald-100 text-emerald-800',
                          cancelled: 'bg-rose-100 text-rose-800'
                        };
                        const statusNames = {
                          pending: 'Chờ nhận',
                          cooking: 'Đang nấu',
                          ready: 'Món xong',
                          served: 'Đã lên bàn',
                          completed: 'Đã thanh toán',
                          cancelled: 'Đã hủy'
                        };

                        const dateStr = new Date(o.created_at).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) + ' ' + new Date(o.created_at).toLocaleDateString('vi-VN');

                        return (
                          <tr key={o.id} className="hover:bg-slate-50/70 transition">
                            <td className="px-5 py-3 font-mono font-bold text-slate-800">
                              #{o.id.substring(0, 8)}
                            </td>
                            <td className="px-5 py-3">
                              <span className="font-bold text-slate-800 px-2.5 py-1 rounded-lg bg-slate-100 border border-slate-200">
                                {o.table ? `Bàn ${o.table.table_number}` : 'Mang về'}
                              </span>
                            </td>
                            <td className="px-5 py-3 text-slate-500">
                              {dateStr}
                            </td>
                            <td className="px-5 py-3">
                              <span className="font-semibold text-slate-700">
                                {o.items ? o.items.length : 0} món
                              </span>
                              <span className="text-[11px] text-slate-400 block line-clamp-1">
                                {o.items ? o.items.map(i => i.product_name).join(', ') : ''}
                              </span>
                            </td>
                            <td className="px-5 py-3 font-extrabold text-emerald-600 text-sm">
                              {o.total_amount?.toLocaleString('vi-VN')} đ
                            </td>
                            <td className="px-5 py-3 text-slate-600">
                              {o.creator?.full_name || 'Phục vụ'}
                              <span className="text-[10px] text-slate-400 block capitalize">({o.creator?.role || 'waiter'})</span>
                            </td>
                            <td className="px-5 py-3">
                              <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold uppercase ${statusColors[o.status] || 'bg-slate-100'}`}>
                                {statusNames[o.status] || o.status}
                              </span>
                            </td>
                            <td className="px-5 py-3 text-right">
                              <button
                                type="button"
                                onClick={() => setSelectedOrderDetail(o)}
                                className="px-3 py-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 font-bold rounded-lg transition border border-emerald-200 text-xs inline-flex items-center space-x-1 cursor-pointer"
                              >
                                <i className="fa-solid fa-eye"></i>
                                <span>Xem đơn</span>
                              </button>
                            </td>
                          </tr>
                        );
                      })
                    )}
                  </tbody>
                </table>
              </div>

              {/* THANH PHÂN TRANG ĐƠN HÀNG (Order Backend Pagination Bar) */}
              <div className="p-3.5 bg-slate-50/70 border-t border-slate-100 flex flex-wrap items-center justify-between gap-3 text-xs">
                <div className="text-slate-500 flex items-center space-x-1">
                  <span>Trang <b className="text-slate-800">{orderPage}</b> / {orderPagination.totalPages || 1}</span>
                  <span className="text-slate-300 mx-1">•</span>
                  <span>Tổng cộng: <b className="text-slate-800">{orderPagination.total}</b> đơn hàng</span>
                </div>

                {/* Các nút chuyển trang */}
                <div className="flex items-center space-x-1.5">
                  <button
                    type="button"
                    disabled={orderPage <= 1}
                    onClick={() => {
                      const newP = Math.max(1, orderPage - 1);
                      setOrderPage(newP);
                      fetchOrders(newP, orderLimit, orderFilterStatus, orderSearchText);
                    }}
                    className="px-2.5 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-100 disabled:opacity-40 disabled:hover:bg-transparent font-medium flex items-center space-x-1 transition cursor-pointer"
                  >
                    <i className="fa-solid fa-chevron-left text-[10px]"></i>
                    <span className="hidden sm:inline">Trước</span>
                  </button>

                  {Array.from({ length: orderPagination.totalPages || 1 }, (_, i) => i + 1).map((pageNum) => (
                    <button
                      key={pageNum}
                      type="button"
                      onClick={() => {
                        setOrderPage(pageNum);
                        fetchOrders(pageNum, orderLimit, orderFilterStatus, orderSearchText);
                      }}
                      className={`w-7 h-7 rounded-lg font-bold transition flex items-center justify-center cursor-pointer ${
                        orderPage === pageNum
                          ? 'bg-emerald-600 text-white shadow-sm'
                          : 'hover:bg-slate-100 text-slate-700'
                      }`}
                    >
                      {pageNum}
                    </button>
                  ))}

                  <button
                    type="button"
                    disabled={orderPage >= orderPagination.totalPages}
                    onClick={() => {
                      const newP = Math.min(orderPagination.totalPages, orderPage + 1);
                      setOrderPage(newP);
                      fetchOrders(newP, orderLimit, orderFilterStatus, orderSearchText);
                    }}
                    className="px-2.5 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-100 disabled:opacity-40 disabled:hover:bg-transparent font-medium flex items-center space-x-1 transition cursor-pointer"
                  >
                    <span className="hidden sm:inline">Sau</span>
                    <i className="fa-solid fa-chevron-right text-[10px]"></i>
                  </button>
                </div>

                {/* Tùy chọn số lượng đơn trên 1 trang */}
                <div className="flex items-center space-x-1.5 text-slate-500">
                  <span className="text-[11px]">Hiển thị:</span>
                  {[5, 10, 20].map((sz) => (
                    <button
                      key={sz}
                      type="button"
                      onClick={() => {
                        setOrderLimit(sz);
                        setOrderPage(1);
                        fetchOrders(1, sz, orderFilterStatus, orderSearchText);
                      }}
                      className={`px-2 py-0.5 rounded text-[11px] font-bold transition cursor-pointer ${
                        orderLimit === sz
                          ? 'bg-slate-800 text-white'
                          : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                      }`}
                    >
                      {sz}
                    </button>
                  ))}
                  <span className="text-[11px]">đơn</span>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* TAB 3: KITCHEN (Chỉ Kitchen, Admin, Manager) */}
        {activeTab === 'kitchen' && (isAdminOrManager || role === 'kitchen') && (
          <div className="space-y-4">
            <div className="flex justify-between items-center bg-white p-4 rounded-xl shadow-sm border border-slate-100">
              <h3 className="font-bold text-slate-700 text-sm flex items-center">
                <i className="fa-solid fa-fire text-amber-500 mr-2"></i>Đơn Đang
                Chờ Bếp Chế Biến
              </h3>
              <button
                onClick={fetchOrders}
                className="text-xs text-emerald-600 hover:underline flex items-center"
              >
                <i className="fa-solid fa-rotate-right mr-1"></i>Làm mới
              </button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {orders
                .filter((o) =>
                  ['pending', 'cooking', 'ready', 'served'].includes(o.status)
                )
                .map((o) => (
                  <div
                    key={o.id}
                    className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex flex-col justify-between"
                  >
                    <div>
                      <div className="flex justify-between items-center mb-2 pb-2 border-b border-slate-100">
                        <div>
                          <span className="font-bold text-xs text-slate-800">
                            #{o.id.substring(0, 8)}...
                          </span>
                          <span className="text-xs text-slate-500 ml-2">
                            {o.table
                              ? 'Bàn ' + o.table.table_number
                              : 'Mang về'}
                          </span>
                        </div>
                        <span
                          className={`text-[10px] px-2 py-0.5 rounded font-bold uppercase ${
                            o.status === 'pending'
                              ? 'bg-amber-100 text-amber-800'
                              : o.status === 'cooking'
                              ? 'bg-orange-100 text-orange-800'
                              : o.status === 'ready'
                              ? 'bg-blue-100 text-blue-800'
                              : 'bg-emerald-100 text-emerald-800'
                          }`}
                        >
                          {o.status}
                        </span>
                      </div>
                      <ul className="text-xs space-y-1.5 my-2">
                        {o.items.map((it) => (
                          <li
                            key={it.id}
                            className="flex justify-between text-slate-600"
                          >
                            <span>{it.product_name}</span>
                            <span className="font-bold">x{it.quantity}</span>
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div className="pt-3 border-t border-slate-50 mt-3 flex space-x-2">
                      {o.status === 'pending' && (
                        <button
                          onClick={() => handleChangeStatus(o.id, 'cooking')}
                          className="flex-1 bg-amber-500 hover:bg-amber-600 text-white font-bold py-1.5 rounded-lg text-xs"
                        >
                          <i className="fa-solid fa-fire mr-1"></i>Nhận Nấu
                        </button>
                      )}
                      {o.status === 'cooking' && (
                        <button
                          onClick={() => handleChangeStatus(o.id, 'ready')}
                          className="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-bold py-1.5 rounded-lg text-xs"
                        >
                          <i className="fa-solid fa-bell mr-1"></i>Món Đã Xong
                        </button>
                      )}
                      {o.status === 'ready' && (
                        <button
                          onClick={() => handleChangeStatus(o.id, 'served')}
                          className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-1.5 rounded-lg text-xs"
                        >
                          <i className="fa-solid fa-check mr-1"></i>Đã Lên Bàn
                        </button>
                      )}
                      {isAdminOrManager && o.status === 'pending' && (
                        <button
                          onClick={() => handleChangeStatus(o.id, 'cancelled')}
                          className="bg-red-50 text-red-600 hover:bg-red-100 font-bold px-2.5 py-1.5 rounded-lg text-xs"
                          title="Hủy đơn & hoàn kho"
                        >
                          Hủy
                        </button>
                      )}
                    </div>
                  </div>
                ))}
            </div>
          </div>
        )}

        {/* TAB 3: CASHIER (Chỉ Cashier, Admin, Manager) */}
        {activeTab === 'cashier' && (isAdminOrManager || role === 'cashier') && (
          <div className="space-y-6">
            {/* Sơ đồ bàn & Đón khách nhanh tại quầy Thu Ngân */}
            <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-100 space-y-4">
              <div className="flex flex-wrap justify-between items-center gap-2">
                <div>
                  <h3 className="font-bold text-slate-800 text-sm flex items-center">
                    <i className="fa-solid fa-chair text-indigo-600 mr-2"></i>
                    Sơ Đồ Bàn & Điều Phối Xếp Bàn Tại Quầy
                  </h3>
                  <p className="text-[11px] text-slate-400 mt-0.5">
                    Thu ngân có thể đón khách, xếp bàn bận hoặc giải phóng bàn ngay tại quầy
                  </p>
                </div>
                <div className="flex items-center space-x-3 text-xs">
                  <span className="flex items-center text-slate-600">
                    <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 mr-1.5"></span>
                    Trống
                  </span>
                  <span className="flex items-center text-slate-600">
                    <span className="w-2.5 h-2.5 rounded-full bg-rose-500 mr-1.5"></span>
                    Đang có khách
                  </span>
                  <span className="flex items-center text-slate-600">
                    <span className="w-2.5 h-2.5 rounded-full bg-amber-500 mr-1.5"></span>
                    Đặt trước
                  </span>
                </div>
              </div>

              {/* Grid bàn thu gọn cho quầy thu ngân */}
              <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-8 gap-2.5">
                {tables.map((t) => {
                  const isSel = selectedTable?.id === t.id;
                  const isOcc = t.status === 'occupied';
                  const isRes = t.status === 'reserved';

                  let cardClass = 'bg-emerald-50/50 text-emerald-900 border-emerald-200 hover:border-emerald-400';
                  let statusLabel = 'Trống';

                  if (isOcc) {
                    cardClass = 'bg-rose-50/60 text-rose-900 border-rose-200 hover:border-rose-300';
                    statusLabel = 'Bận';
                  } else if (isRes) {
                    cardClass = 'bg-amber-50/60 text-amber-900 border-amber-200 hover:border-amber-300';
                    statusLabel = 'Đặt';
                  }

                  if (isSel) {
                    cardClass = 'ring-2 ring-indigo-600 bg-white border-indigo-600 shadow-md shadow-indigo-100';
                  }

                  return (
                    <button
                      key={t.id}
                      type="button"
                      onClick={() => setSelectedTable(t)}
                      className={`p-2.5 rounded-xl border text-center transition flex flex-col items-center justify-between cursor-pointer ${cardClass}`}
                    >
                      <div className="w-full flex justify-between items-center text-[10px] text-slate-400">
                        <span>{t.capacity}c</span>
                        <span className="font-bold">{statusLabel}</span>
                      </div>
                      <i className={`fa-solid fa-chair text-lg my-1 ${isOcc ? 'text-rose-500' : isRes ? 'text-amber-500' : 'text-emerald-600'}`}></i>
                      <span className="font-bold text-xs text-slate-800">
                        Bàn {t.table_number}
                      </span>
                    </button>
                  );
                })}
              </div>

              {/* Thanh tác vụ nhanh bàn của Thu Ngân */}
              {selectedTable && (
                <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 flex flex-wrap items-center justify-between gap-3">
                  <div className="flex items-center space-x-2">
                    <div className="w-7 h-7 rounded-lg bg-indigo-600 text-white flex items-center justify-center font-bold text-xs">
                      {selectedTable.table_number}
                    </div>
                    <div>
                      <span className="text-xs font-bold text-slate-800">
                        Bàn {selectedTable.table_number} ({selectedTable.capacity} chỗ) - Trạng thái: {selectedTable.status === 'occupied' ? '🔴 Đang có khách' : selectedTable.status === 'reserved' ? '🟡 Đã đặt trước' : '🟢 Bàn trống'}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center space-x-2">
                    {selectedTable.status === 'available' && (
                      <>
                        <button
                          type="button"
                          onClick={() => handleUpdateTableStatus(selectedTable.id, 'occupied')}
                          className="px-3 py-1.5 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-lg shadow-sm transition flex items-center space-x-1"
                        >
                          <i className="fa-solid fa-user-plus mr-1"></i>
                          <span>Xếp Khách Vào Bàn (Set Bận)</span>
                        </button>
                        <button
                          type="button"
                          onClick={() => handleUpdateTableStatus(selectedTable.id, 'reserved')}
                          className="px-2.5 py-1.5 bg-amber-500 hover:bg-amber-600 text-white text-xs font-medium rounded-lg shadow-sm transition"
                        >
                          <span>Đặt trước</span>
                        </button>
                      </>
                    )}

                    {selectedTable.status === 'occupied' && (
                      <button
                        type="button"
                        onClick={() => handleUpdateTableStatus(selectedTable.id, 'available')}
                        className="px-3 py-1.5 bg-slate-600 hover:bg-slate-700 text-white text-xs font-bold rounded-lg shadow-sm transition flex items-center space-x-1"
                      >
                        <i className="fa-solid fa-arrow-rotate-left mr-1"></i>
                        <span>Trả bàn trống</span>
                      </button>
                    )}

                    {selectedTable.status === 'reserved' && (
                      <>
                        <button
                          type="button"
                          onClick={() => handleUpdateTableStatus(selectedTable.id, 'occupied')}
                          className="px-3 py-1.5 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-lg shadow-sm transition"
                        >
                          <i className="fa-solid fa-check mr-1"></i>
                          <span>Khách đến (Set Bận)</span>
                        </button>
                        <button
                          type="button"
                          onClick={() => handleUpdateTableStatus(selectedTable.id, 'available')}
                          className="px-2.5 py-1.5 bg-slate-500 hover:bg-slate-600 text-white text-xs font-medium rounded-lg shadow-sm transition"
                        >
                          <span>Hủy đặt</span>
                        </button>
                      </>
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* Hóa đơn cần thanh toán */}
            <div className="flex justify-between items-center bg-white p-4 rounded-xl shadow-sm border border-slate-100">
              <h3 className="font-bold text-slate-700 text-sm flex items-center">
                <i className="fa-solid fa-receipt text-indigo-500 mr-2"></i>Hóa
                Đơn Cần Thanh Toán
              </h3>
              <button
                onClick={fetchOrders}
                className="text-xs text-emerald-600 hover:underline flex items-center"
              >
                <i className="fa-solid fa-rotate-right mr-1"></i>Làm mới
              </button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {orders
                .filter(
                  (o) => o.status !== 'completed' && o.status !== 'cancelled'
                )
                .map((o) => (
                  <div
                    key={o.id}
                    className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex flex-col justify-between"
                  >
                    <div>
                      <div className="flex justify-between items-center mb-2 pb-2 border-b border-slate-100">
                        <div>
                          <span className="font-bold text-xs text-slate-800">
                            Đơn #{o.id.substring(0, 8)}...
                          </span>
                          <span className="text-xs bg-slate-100 px-2 py-0.5 rounded font-semibold ml-2">
                            {o.table
                              ? 'Bàn ' + o.table.table_number
                              : 'Mang về'}
                          </span>
                        </div>
                        <span className="text-xs font-bold text-emerald-600 uppercase">
                          {o.status}
                        </span>
                      </div>
                      <div className="text-xs space-y-1 my-2">
                        {o.items.map((it) => (
                          <div
                            key={it.id}
                            className="flex justify-between text-slate-600"
                          >
                            <span>
                              {it.product_name} x{it.quantity}
                            </span>
                            <span>{it.subtotal.toLocaleString('vi-VN')} đ</span>
                          </div>
                        ))}
                      </div>
                    </div>
                    <div className="pt-3 border-t border-slate-50 mt-3">
                      <div className="flex justify-between items-center mb-3">
                        <span className="text-xs text-slate-500">
                          Cần thanh toán:
                        </span>
                        <span className="font-bold text-base text-red-600">
                          {o.total_amount.toLocaleString('vi-VN')} đ
                        </span>
                      </div>
                      <button
                        onClick={() => handlePayment(o.id)}
                        className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2 rounded-xl text-xs shadow-md shadow-emerald-100 transition flex items-center justify-center space-x-1"
                      >
                        <i className="fa-solid fa-receipt mr-1"></i>
                        <span>Xác Nhận Đã Thanh Toán & Bắn Mail</span>
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        )}

        {/* TAB 4: QUẢN LÝ KHO & MÓN ĂN (CHỈ ADMIN & MANAGER) */}
        {activeTab === 'inventory' && isAdminOrManager && (
          <div className="space-y-4">
            <div className="flex justify-between items-center bg-white p-4 rounded-xl shadow-sm border border-slate-100">
              <div>
                <h3 className="font-bold text-slate-800 text-sm flex items-center">
                  <i className="fa-solid fa-boxes-stacked text-emerald-600 mr-2"></i>Quản Lý Thực Đơn & Kho Hàng
                </h3>
                <p className="text-xs text-slate-400 mt-0.5">Cập nhật số lượng tồn kho (tự động xóa cache Redis), bật/tắt phục vụ món</p>
              </div>
              <div className="flex items-center space-x-3">
                <button
                  onClick={() => setShowAddProductModal(true)}
                  className="bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold px-3 py-2 rounded-lg shadow-sm flex items-center space-x-1"
                >
                  <i className="fa-solid fa-plus"></i>
                  <span>Thêm Món Mới</span>
                </button>
                <button onClick={fetchProducts} className="text-xs text-emerald-600 hover:underline flex items-center">
                  <i className="fa-solid fa-rotate-right mr-1"></i>Làm mới
                </button>
              </div>
            </div>

            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs text-slate-600">
                  <thead className="bg-slate-50 text-slate-700 uppercase font-bold border-b border-slate-100">
                    <tr>
                      <th className="px-5 py-3 w-16">Hình Ảnh</th>
                      <th className="px-5 py-3">Tên Món Ăn</th>
                      <th className="px-5 py-3">Danh Mục</th>
                      <th className="px-5 py-3">Giá Bán</th>
                      <th className="px-5 py-3">Trạng Thái</th>
                      <th className="px-5 py-3">Tồn Kho Hiện Tại</th>
                      <th className="px-5 py-3 text-right">Điều Chỉnh Kho Nhanh</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {products.map(p => (
                      <tr key={p.id} className="hover:bg-slate-50/50 transition">
                        <td className="px-5 py-2.5">
                          <img
                            src={getProductImage(p)}
                            alt={p.name}
                            onClick={() => {
                              setSelectedProductDetail(p);
                              setDetailQuantity(1);
                            }}
                            className="w-11 h-11 rounded-xl object-cover border border-slate-200 cursor-pointer hover:scale-105 transition shadow-sm"
                            title="Bấm để xem chi tiết"
                          />
                        </td>
                        <td className="px-5 py-3 font-semibold text-slate-800">
                          <span
                            onClick={() => {
                              setSelectedProductDetail(p);
                              setDetailQuantity(1);
                            }}
                            className="hover:text-emerald-600 cursor-pointer transition"
                          >
                            {p.name}
                          </span>
                          <p className="text-[11px] text-slate-400 font-normal line-clamp-1">{p.description || ''}</p>
                        </td>
                        <td className="px-5 py-3">
                          <span className="bg-slate-100 px-2 py-0.5 rounded text-slate-600">
                            {p.category?.name || 'Món chính'}
                          </span>
                        </td>
                        <td className="px-5 py-3 font-bold text-emerald-600">
                          {p.price?.toLocaleString('vi-VN')} đ
                        </td>
                        <td className="px-5 py-3">
                          <button
                            onClick={() => handleToggleProductStatus(p)}
                            className={`px-2.5 py-1 rounded-full text-[10px] font-bold uppercase transition ${
                              p.status === 'available' ? 'bg-emerald-100 text-emerald-800 hover:bg-emerald-200' : 'bg-red-100 text-red-800 hover:bg-red-200'
                            }`}
                          >
                            {p.status === 'available' ? 'Đang Bán' : 'Tạm Ngưng'}
                          </button>
                        </td>
                        <td className="px-5 py-3">
                          <span className={`font-bold ${p.quantity <= 5 ? 'text-red-500' : 'text-slate-700'}`}>
                            {p.quantity} phần
                          </span>
                        </td>
                        <td className="px-5 py-3 text-right">
                          <div className="inline-flex items-center space-x-1.5">
                            <input
                              type="number"
                              min="0"
                              defaultValue={p.quantity}
                              id={`stock-input-${p.id}`}
                              className="w-16 border rounded px-2 py-1 text-xs text-center focus:outline-none focus:ring-1 focus:ring-emerald-500"
                            />
                            <button
                              onClick={() => {
                                const val = document.getElementById(`stock-input-${p.id}`).value;
                                handleUpdateStock(p.id, val);
                              }}
                              className="bg-slate-800 hover:bg-slate-900 text-white px-2.5 py-1 rounded text-xs font-semibold shadow-sm"
                            >
                              Lưu
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* TAB 5: QUẢN LÝ PHÂN QUYỀN NHÂN VIÊN (CHỈ ADMIN) */}
        {activeTab === 'users' && isAdmin && (
          <div className="space-y-4">
            <div className="flex justify-between items-center bg-white p-4 rounded-xl shadow-sm border border-slate-100">
              <div>
                <h3 className="font-bold text-slate-800 text-sm flex items-center">
                  <i className="fa-solid fa-users-gear text-emerald-600 mr-2"></i>Danh Sách & Phân Quyền Nhân Viên
                </h3>
                <p className="text-xs text-slate-400 mt-0.5">Admin có thể thay đổi trực tiếp vai trò của bất kỳ nhân viên nào</p>
              </div>
              <button onClick={fetchUsers} className="text-xs text-emerald-600 hover:underline flex items-center">
                <i className="fa-solid fa-rotate-right mr-1"></i>Tải lại danh sách
              </button>
            </div>

            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs text-slate-600">
                  <thead className="bg-slate-50 text-slate-700 uppercase font-bold border-b border-slate-100">
                    <tr>
                      <th className="px-5 py-3">Họ và Tên</th>
                      <th className="px-5 py-3">Tài khoản (Username)</th>
                      <th className="px-5 py-3">Email</th>
                      <th className="px-5 py-3">Vai Trò Hiện Tại</th>
                      <th className="px-5 py-3 text-right">Hành Động Đổi Vai Trò</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {usersList.map(u => (
                      <tr key={u.id} className="hover:bg-slate-50/50 transition">
                        <td className="px-5 py-3 font-semibold text-slate-800">{u.full_name || 'Chưa cập nhật'}</td>
                        <td className="px-5 py-3">{u.username}</td>
                        <td className="px-5 py-3 text-slate-500">{u.email}</td>
                        <td className="px-5 py-3">
                          <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold uppercase ${
                            u.role === 'admin' ? 'bg-purple-100 text-purple-800' :
                            u.role === 'manager' ? 'bg-indigo-100 text-indigo-800' :
                            u.role === 'kitchen' ? 'bg-amber-100 text-amber-800' :
                            u.role === 'cashier' ? 'bg-blue-100 text-blue-800' : 'bg-emerald-100 text-emerald-800'
                          }`}>
                            {u.role}
                          </span>
                        </td>
                        <td className="px-5 py-3 text-right">
                          <select
                            defaultValue={u.role}
                            onChange={(e) => handleUpdateRole(u.id, e.target.value)}
                            disabled={u.id === currentUser?.id}
                            className="bg-white border border-slate-200 text-slate-700 text-xs rounded-lg px-2.5 py-1 focus:ring-1 focus:ring-emerald-500 focus:outline-none disabled:bg-slate-100 disabled:cursor-not-allowed"
                          >
                            <option value="waiter">Waiter (Phục vụ)</option>
                            <option value="kitchen">Kitchen (Bếp)</option>
                            <option value="cashier">Cashier (Thu ngân)</option>
                            <option value="manager">Manager (Quản lý)</option>
                            <option value="admin">Admin (Quản trị viên)</option>
                          </select>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* MODAL THÊM MÓN MỚI (ADMIN/MANAGER) */}
      {showAddProductModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white w-full max-w-md rounded-2xl shadow-xl p-6 border border-slate-100">
            <div className="flex justify-between items-center mb-4 pb-2 border-b">
              <h3 className="font-bold text-slate-800 text-base">Thêm Món Ăn Mới</h3>
              <button onClick={() => setShowAddProductModal(false)} className="text-slate-400 hover:text-slate-600">
                <i className="fa-solid fa-xmark text-lg"></i>
              </button>
            </div>
            <form onSubmit={handleCreateProduct} className="space-y-3.5">
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Tên món ăn</label>
                <input
                  type="text"
                  required
                  value={newProductData.name}
                  onChange={(e) => setNewProductData({ ...newProductData, name: e.target.value })}
                  className="w-full text-xs px-3 py-2 border rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                  placeholder="Ví dụ: Bò Bít Tết Sốt Nấm"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Giá bán (VNĐ)</label>
                  <input
                    type="number"
                    required
                    min="0"
                    value={newProductData.price}
                    onChange={(e) => setNewProductData({ ...newProductData, price: e.target.value })}
                    className="w-full text-xs px-3 py-2 border rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                    placeholder="150000"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Số lượng kho ban đầu</label>
                  <input
                    type="number"
                    required
                    min="0"
                    value={newProductData.quantity}
                    onChange={(e) => setNewProductData({ ...newProductData, quantity: e.target.value })}
                    className="w-full text-xs px-3 py-2 border rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                  />
                </div>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Danh mục</label>
                <select
                  required
                  value={newProductData.category_id}
                  onChange={(e) => setNewProductData({ ...newProductData, category_id: e.target.value })}
                  className="w-full text-xs px-3 py-2 border rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none bg-white"
                >
                  <option value="">-- Chọn danh mục --</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Mô tả món</label>
                <textarea
                  rows="2"
                  value={newProductData.description}
                  onChange={(e) => setNewProductData({ ...newProductData, description: e.target.value })}
                  className="w-full text-xs px-3 py-2 border rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                  placeholder="Mô tả nguyên liệu, hương vị..."
                ></textarea>
              </div>
              <div className="pt-2 flex space-x-2">
                <button
                  type="button"
                  onClick={() => setShowAddProductModal(false)}
                  className="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-bold py-2 rounded-lg"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold py-2 rounded-lg shadow-sm"
                >
                  Lưu Món Ăn
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Chi Tiết Món Ăn (Product Detail Modal) */}
      {selectedProductDetail && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-fade-in">
          <div className="bg-white max-w-lg w-full rounded-3xl shadow-2xl overflow-hidden border border-slate-100 flex flex-col max-h-[90vh]">
            {/* Ảnh lớn và nút đóng */}
            <div className="relative h-64 sm:h-72 w-full bg-slate-100 shrink-0">
              <img
                src={getProductImage(selectedProductDetail)}
                alt={selectedProductDetail.name}
                className="w-full h-full object-cover"
              />
              <button
                type="button"
                onClick={() => setSelectedProductDetail(null)}
                className="absolute top-4 right-4 w-9 h-9 rounded-full bg-black/50 hover:bg-black/70 text-white flex items-center justify-center transition shadow-lg cursor-pointer"
              >
                <i className="fa-solid fa-xmark text-base"></i>
              </button>
              
              {/* Badge trạng thái tồn kho trên ảnh */}
              <div className="absolute bottom-4 left-4 flex items-center space-x-2">
                <span
                  className={`text-xs px-3 py-1 rounded-full font-bold shadow-md ${
                    selectedProductDetail.quantity > 0 && selectedProductDetail.status === 'available'
                      ? 'bg-emerald-500 text-white'
                      : 'bg-rose-500 text-white'
                  }`}
                >
                  <i className="fa-solid fa-boxes-stacked mr-1"></i>
                  {selectedProductDetail.quantity > 0 && selectedProductDetail.status === 'available'
                    ? `Còn lại trong kho: ${selectedProductDetail.quantity} suất`
                    : 'Tạm ngưng phục vụ'}
                </span>
                {selectedProductDetail.category?.name && (
                  <span className="text-xs px-3 py-1 rounded-full font-bold bg-white/90 text-slate-800 shadow-md">
                    {selectedProductDetail.category.name}
                  </span>
                )}
              </div>
            </div>

            {/* Thông tin chi tiết */}
            <div className="p-6 overflow-y-auto space-y-4">
              <div className="flex justify-between items-start gap-4">
                <div>
                  <h3 className="text-xl font-extrabold text-slate-800">
                    {selectedProductDetail.name}
                  </h3>
                  <p className="text-xs text-slate-400 mt-1">
                    Mã món: <code className="bg-slate-100 px-1 py-0.5 rounded text-slate-600">{selectedProductDetail.id.substring(0, 8)}</code>
                  </p>
                </div>
                <div className="text-right shrink-0">
                  <span className="text-2xl font-black text-emerald-600">
                    {selectedProductDetail.price.toLocaleString('vi-VN')} đ
                  </span>
                  <span className="block text-[11px] text-slate-400">/ 1 phần</span>
                </div>
              </div>

              {/* Mô tả chi tiết món ăn */}
              <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100">
                <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5 flex items-center">
                  <i className="fa-solid fa-circle-info text-emerald-600 mr-1.5"></i>
                  Giới thiệu & Hương vị đặc trưng:
                </h4>
                <p className="text-xs text-slate-600 leading-relaxed">
                  {selectedProductDetail.description ||
                    'Món ăn được tuyển chọn từ các nguyên liệu tươi sống chất lượng cao nhất, tẩm ướp gia vị tinh tế theo công thức độc quyền của bếp trưởng, mang đến trải nghiệm ẩm thực trọn vẹn và đậm đà.'}
                </p>
              </div>

              {/* Bộ chọn số lượng */}
              <div className="flex items-center justify-between pt-2">
                <span className="text-xs font-bold text-slate-700">Chọn số lượng:</span>
                <div className="flex items-center space-x-3 bg-slate-100 p-1.5 rounded-xl">
                  <button
                    type="button"
                    disabled={detailQuantity <= 1}
                    onClick={() => setDetailQuantity(Math.max(1, detailQuantity - 1))}
                    className="w-8 h-8 rounded-lg bg-white text-slate-700 font-bold shadow-sm hover:bg-slate-200 disabled:opacity-40 disabled:hover:bg-white flex items-center justify-center transition"
                  >
                    <i className="fa-solid fa-minus text-xs"></i>
                  </button>
                  <span className="w-8 text-center font-extrabold text-sm text-slate-800">
                    {detailQuantity}
                  </span>
                  <button
                    type="button"
                    disabled={detailQuantity >= selectedProductDetail.quantity}
                    onClick={() => setDetailQuantity(Math.min(selectedProductDetail.quantity, detailQuantity + 1))}
                    className="w-8 h-8 rounded-lg bg-white text-slate-700 font-bold shadow-sm hover:bg-slate-200 disabled:opacity-40 disabled:hover:bg-white flex items-center justify-center transition"
                  >
                    <i className="fa-solid fa-plus text-xs"></i>
                  </button>
                </div>
              </div>
            </div>

            {/* Footer hành động */}
            <div className="p-4 bg-slate-50 border-t border-slate-100 flex items-center space-x-3">
              <button
                type="button"
                onClick={() => setSelectedProductDetail(null)}
                className="px-4 py-2.5 rounded-xl border border-slate-200 text-slate-600 text-xs font-bold hover:bg-slate-100 transition"
              >
                Đóng
              </button>
              <button
                type="button"
                disabled={selectedProductDetail.quantity <= 0 || selectedProductDetail.status !== 'available'}
                onClick={() => {
                  const success = addToCart(selectedProductDetail, detailQuantity);
                  if (success) {
                    showToast(`🎉 Đã thêm ${detailQuantity} phần ${selectedProductDetail.name} vào đơn!`);
                    setSelectedProductDetail(null);
                  }
                }}
                className="flex-1 py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-200 disabled:text-slate-400 text-white text-xs font-bold rounded-xl shadow-lg shadow-emerald-200 transition flex items-center justify-center space-x-2"
              >
                <i className="fa-solid fa-cart-plus"></i>
                <span>
                  Thêm vào đơn ({detailQuantity} phần) - {(selectedProductDetail.price * detailQuantity).toLocaleString('vi-VN')} đ
                </span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Chi Tiết Đơn Hàng (Order Detail Modal) */}
      {selectedOrderDetail && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-fade-in">
          <div className="bg-white max-w-lg w-full rounded-3xl shadow-2xl overflow-hidden border border-slate-100 flex flex-col max-h-[90vh]">
            {/* Header Hóa đơn */}
            <div className="p-5 bg-slate-800 text-white flex justify-between items-center">
              <div>
                <span className="text-[10px] font-bold tracking-wider text-slate-400 uppercase">
                  Chi Tiết Đơn Hàng
                </span>
                <h3 className="text-base font-extrabold flex items-center space-x-2">
                  <span>#{selectedOrderDetail.id.substring(0, 8)}...</span>
                  <span className="text-xs bg-emerald-500/30 text-emerald-300 px-2 py-0.5 rounded-full">
                    {selectedOrderDetail.table ? `Bàn ${selectedOrderDetail.table.table_number}` : 'Mang về'}
                  </span>
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setSelectedOrderDetail(null)}
                className="w-8 h-8 rounded-full bg-slate-700 hover:bg-slate-600 text-slate-300 hover:text-white flex items-center justify-center transition cursor-pointer"
              >
                <i className="fa-solid fa-xmark text-sm"></i>
              </button>
            </div>

            {/* Nội dung hóa đơn */}
            <div className="p-6 overflow-y-auto space-y-4">
              {/* Thông tin chung */}
              <div className="grid grid-cols-2 gap-3 text-xs bg-slate-50 p-3.5 rounded-2xl border border-slate-100">
                <div>
                  <span className="text-slate-400 block text-[11px]">Thời gian đặt:</span>
                  <span className="font-bold text-slate-700">
                    {new Date(selectedOrderDetail.created_at).toLocaleTimeString('vi-VN')} - {new Date(selectedOrderDetail.created_at).toLocaleDateString('vi-VN')}
                  </span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[11px]">Nhân viên phụ trách:</span>
                  <span className="font-bold text-slate-700">
                    {selectedOrderDetail.creator?.full_name || 'Phục vụ'} ({selectedOrderDetail.creator?.role || 'waiter'})
                  </span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[11px]">Trạng thái món:</span>
                  <span className="font-bold uppercase text-emerald-600">
                    {selectedOrderDetail.status}
                  </span>
                </div>
                <div>
                  <span className="text-slate-400 block text-[11px]">Thanh toán:</span>
                  <span className={`font-bold ${selectedOrderDetail.status === 'completed' ? 'text-emerald-600' : 'text-amber-600'}`}>
                    {selectedOrderDetail.status === 'completed' ? 'Đã thanh toán' : 'Chưa thanh toán'}
                  </span>
                </div>
              </div>

              {/* Bảng danh sách các món trong đơn */}
              <div>
                <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center">
                  <i className="fa-solid fa-utensils text-emerald-600 mr-1.5"></i>
                  Danh sách món ăn ({selectedOrderDetail.items?.length || 0} món):
                </h4>
                <div className="border border-slate-100 rounded-2xl overflow-hidden divide-y divide-slate-100">
                  {selectedOrderDetail.items?.map((it, idx) => (
                    <div key={it.id || idx} className="p-3 flex justify-between items-center text-xs hover:bg-slate-50/50">
                      <div className="flex-1 pr-3">
                        <span className="font-bold text-slate-800">{it.product_name}</span>
                        <div className="text-[11px] text-slate-400 flex items-center space-x-2 mt-0.5">
                          <span>{it.unit_price?.toLocaleString('vi-VN')} đ / phần</span>
                          {it.note && <span className="text-amber-600 italic">({it.note})</span>}
                        </div>
                      </div>
                      <div className="text-center font-bold text-slate-700 px-3">
                        x{it.quantity}
                      </div>
                      <div className="text-right font-extrabold text-slate-800 w-24">
                        {(it.subtotal || (it.unit_price * it.quantity))?.toLocaleString('vi-VN')} đ
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Ghi chú đơn nếu có */}
              {selectedOrderDetail.note && (
                <div className="p-3 bg-amber-50 rounded-xl border border-amber-200 text-xs text-amber-800">
                  <span className="font-bold mr-1"><i className="fa-solid fa-note-sticky mr-1"></i>Ghi chú:</span>
                  {selectedOrderDetail.note}
                </div>
              )}

              {/* Tổng kết tiền */}
              <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100 space-y-2">
                <div className="flex justify-between items-center text-xs text-slate-500">
                  <span>Tạm tính món ăn:</span>
                  <span>{selectedOrderDetail.total_amount?.toLocaleString('vi-VN')} đ</span>
                </div>
                <div className="flex justify-between items-center text-xs text-slate-500">
                  <span>Thuế VAT & Phí dịch vụ:</span>
                  <span>0 đ</span>
                </div>
                <div className="flex justify-between items-center pt-2 border-t border-slate-200 text-sm">
                  <span className="font-bold text-slate-800">TỔNG CỘNG THANH TOÁN:</span>
                  <span className="text-lg font-black text-rose-600">
                    {selectedOrderDetail.total_amount?.toLocaleString('vi-VN')} đ
                  </span>
                </div>
              </div>
            </div>

            {/* Footer */}
            <div className="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-between">
              <button
                type="button"
                onClick={() => setSelectedOrderDetail(null)}
                className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-xs font-bold hover:bg-slate-100 transition"
              >
                Đóng
              </button>

              <button
                type="button"
                onClick={() => {
                  window.print();
                }}
                className="px-4 py-2 bg-slate-800 hover:bg-slate-900 text-white text-xs font-bold rounded-xl transition flex items-center space-x-1.5 shadow-sm"
              >
                <i className="fa-solid fa-print"></i>
                <span>In Hóa Đơn</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toast Notification */}
      {toast.show && (
        <div
          className={`fixed bottom-5 right-5 z-50 px-5 py-3 rounded-xl shadow-xl flex items-center space-x-3 transition-all ${
            toast.isError ? 'bg-red-600 text-white' : 'bg-slate-900 text-white'
          }`}
        >
          <i
            className={`fa-solid ${
              toast.isError
                ? 'fa-triangle-exclamation'
                : 'fa-circle-check text-emerald-400'
            } text-lg`}
          ></i>
          <span className="text-xs font-medium">{toast.msg}</span>
        </div>
      )}
    </div>
  );
}
