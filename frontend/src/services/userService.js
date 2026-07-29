import api from "./api";

const getAllUsers = async () => {
    const response = await api.get("/users");
    return response.data;
};

const getUserById = async (id) => {
    const response = await api.get(`/users/${id}`);
    return response.data;
};

const createUser = async (userData) => {
    const response = await api.post("/users", userData);
    return response.data;
};

const updateUser = async (id, userData) => {
    const response = await api.put(`/users/${id}`, userData);
    return response.data;
};

// Kullanıcıyı siler.
// force=true gönderilirse kullanıcıya ait belgeler de silinir.
const deleteUser = async (id, force = false) => {

    await api.delete(`/users/${id}?force=${force}`);

};

export default {
    getAllUsers,
    getUserById,
    createUser,
    updateUser,
    deleteUser,
};