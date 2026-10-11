let products = [
    { id: 1, category_id: 1, name: 'Phở bò', description: 'Phở bò truyền thống nạm gầu', price: 50000, quantity: 50, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 2, category_id: 1, name: 'Phở gà', description: 'Phở gà đùi lá chanh', price: 45000, quantity: 40, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 3, category_id: 2, name: 'Bún chả Hà Nội', description: 'Bún chả nướng than hoa', price: 55000, quantity: 30, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 4, category_id: 2, name: 'Bún bò Huế', description: 'Bún bò Huế đầy đủ chả cua', price: 60000, quantity: 20, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 5, category_id: 3, name: 'Cơm tấm sườn bì chả', description: 'Cơm tấm đặc biệt', price: 65000, quantity: 60, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 6, category_id: 3, name: 'Cơm chiên hải sản', description: 'Cơm chiên tôm, mực, chả', price: 70000, quantity: 25, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 7, category_id: 4, name: 'Bánh mì xíu mại', description: 'Bánh mì giòn và xíu mại đà lạt', price: 25000, quantity: 100, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 8, category_id: 4, name: 'Bánh mì thịt nướng', description: 'Bánh mì thịt nướng sả', price: 30000, quantity: 80, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 9, category_id: 5, name: 'Gỏi cuốn tôm thịt', description: 'Gỏi cuốn 3 con tôm to', price: 15000, quantity: 50, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 10, category_id: 5, name: 'Chả giò rế hải sản', description: 'Chả giò chiên giòn', price: 40000, quantity: 0, status: 'unavailable', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 11, category_id: 6, name: 'Bánh xèo miền Tây', description: 'Bánh xèo nhân tôm thịt', price: 45000, quantity: 35, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 12, category_id: 6, name: 'Bánh khọt Vũng Tàu', description: 'Bánh khọt tôm mực', price: 50000, quantity: 40, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 13, category_id: 2, name: 'Bún đậu mắm tôm', description: 'Mẹt bún đậu đầy đủ', price: 55000, quantity: 30, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 14, category_id: 2, name: 'Mì Quảng', description: 'Mì Quảng ếch tôm thịt', price: 60000, quantity: 20, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 15, category_id: 2, name: 'Bánh canh cua', description: 'Bánh canh cua biển nguyên con', price: 80000, quantity: 15, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 16, category_id: 2, name: 'Hủ tiếu Nam Vang', description: 'Hủ tiếu nước đầy đủ', price: 65000, quantity: 45, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 17, category_id: 1, name: 'Bò kho', description: 'Bò kho ăn kèm bánh mì', price: 55000, quantity: 0, status: 'unavailable', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 18, category_id: 2, name: 'Bún riêu cua', description: 'Bún riêu cua đồng xịn', price: 45000, quantity: 50, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 19, category_id: 6, name: 'Bánh cuốn Thanh Trì', description: 'Bánh cuốn nóng chả lụa', price: 35000, quantity: 60, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 20, category_id: 3, name: 'Cơm thố gà nướng', description: 'Cơm thố chiên giòn, đùi gà', price: 75000, quantity: 20, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 21, category_id: 2, name: 'Bún thịt nướng', description: 'Bún thịt nướng chả giò', price: 40000, quantity: 55, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 22, category_id: 7, name: 'Lẩu Thái hải sản', description: 'Lẩu Thái chua cay đủ món', price: 250000, quantity: 10, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 23, category_id: 7, name: 'Gà nướng muối ớt', description: 'Gà ta nướng nguyên con', price: 180000, quantity: 5, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 24, category_id: 7, name: 'Sườn xào chua ngọt', description: 'Sườn non xào cay ngọt', price: 120000, quantity: 15, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
    { id: 25, category_id: 8, name: 'Chè khúc bạch', description: 'Chè tráng miệng thanh mát', price: 25000, quantity: 100, status: 'available', created_at: new Date().toISOString(), updated_at: new Date().toISOString() }
];

const generateId = () => {
    return products.length > 0 ? Math.max(...products.map(p => p.id)) + 1 : 1;
};

const getProducts = (page = 1, limit = 10) => {
    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + limit;
    const paginatedProducts = products.slice(startIndex, endIndex);
    
    return {
        page,
        limit,
        total: products.length,
        totalPages: Math.ceil(products.length / limit),
        data: paginatedProducts
    };
};

const getProductById = (id) => {
    return products.find(p => p.id === id);
};

const createProduct = (data) => {
    const { category_id, name, description, price, quantity, status } = data;
    const newProduct = {
        id: generateId(),
        category_id: category_id || null,
        name,
        description: description || '',
        price,
        quantity: quantity || 0,
        status: status || (quantity === 0 ? 'unavailable' : 'available'),
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString()
    };
    products.push(newProduct);
    return newProduct;
};

const updateProduct = (id, data) => {
    const index = products.findIndex(p => p.id === id);
    if (index === -1) return null;
    
    const { category_id, name, description, price, quantity, status } = data;
    
    products[index] = {
        ...products[index],
        category_id: category_id !== undefined ? category_id : products[index].category_id,
        name: name || products[index].name,
        description: description !== undefined ? description : products[index].description,
        price: price !== undefined ? price : products[index].price,
        quantity: quantity !== undefined ? quantity : products[index].quantity,
        status: status || products[index].status,
        updated_at: new Date().toISOString()
    };
    
    if (products[index].quantity === 0) {
        products[index].status = 'unavailable';
    } else if (products[index].status === 'unavailable' && products[index].quantity > 0) {
        products[index].status = 'available';
    }
    
    return products[index];
};

const deleteProduct = (id) => {
    const index = products.findIndex(p => p.id === id);
    if (index !== -1) {
        const deletedProduct = products.splice(index, 1);
        return deletedProduct[0];
    }
    return null;
};

module.exports = {
    getProducts,
    getProductById,
    createProduct,
    updateProduct,
    deleteProduct
};
