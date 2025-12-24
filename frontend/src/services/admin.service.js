import api from "./api";

const getAllUsers = () => {
    return api.get("/admin/users");
};

const AdminService = {
    getAllUsers,
};

export default AdminService;