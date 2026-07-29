import { useState } from "react";

import userService from "../../services/userService";

function UserModal({

                       selectedUser,

                       onClose,

                       onSuccess

                   }) {

    // Formdaki hata mesajını tutar.
    const [error, setError] = useState("");

    // Form verilerini tutar.
    const [formData, setFormData] = useState({

        firstName: selectedUser?.firstName || "",

        lastName: selectedUser?.lastName || "",

        username: selectedUser?.username || "",

        email: selectedUser?.email || "",

        password: "",

        roleId:
            selectedUser?.role === "ADMIN"
                ? 1
                : selectedUser?.role === "MANAGER"
                    ? 2
                    : 3

    });

    // Input değişikliklerini form state'ine yazar.
    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData({

            ...formData,

            [name]: name === "roleId" ? Number(value) : value

        });

    };

    // Form gönderildiğinde kullanıcı ekler veya günceller.
    const handleSubmit = async (event) => {

        event.preventDefault();

        setError("");

        try {

            if (selectedUser) {

                // Kullanıcı günceller.
                await userService.updateUser(

                    selectedUser.id,

                    {

                        firstName: formData.firstName,

                        lastName: formData.lastName,

                        username: formData.username,

                        email: formData.email,

                        roleId: formData.roleId

                    }

                );

            } else {

                // Yeni kullanıcı oluşturur.
                await userService.createUser(formData);

            }

            // Listeyi yeniler.
            await onSuccess();

            // Modalı kapatır.
            onClose();

        } catch (error) {

            console.error(error);

            // Backend'den gelen hata mesajını gösterir.
            if (error.response?.data?.message) {

                setError(error.response.data.message);

            } else {

                setError("Operation failed.");

            }

        }

    };

    return (

        <div
            className="modal d-block"
            style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        >

            <div className="modal-dialog">

                <div className="modal-content">

                    <div className="modal-header">

                        {/* Modal başlığı */}
                        <h5 className="modal-title">

                            {

                                selectedUser

                                    ? "Edit User"

                                    : "Add User"

                            }

                        </h5>

                        <button
                            type="button"
                            className="btn-close"
                            onClick={onClose}
                        />

                    </div>

                    <form onSubmit={handleSubmit}>

                        <div className="modal-body">

                            {

                                error && (

                                    <div className="alert alert-danger">

                                        {error}

                                    </div>

                                )

                            }

                            <input
                                className="form-control mb-3"
                                placeholder="First Name"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                            />

                            <input
                                className="form-control mb-3"
                                placeholder="Last Name"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                            />

                            <input
                                className="form-control mb-3"
                                placeholder="Username"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                required
                            />

                            <input
                                className="form-control mb-3"
                                placeholder="Email"
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                            />

                            {

                                !selectedUser && (

                                    <input
                                        className="form-control mb-3"
                                        placeholder="Password"
                                        type="password"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                    />

                                )

                            }

                            <select
                                className="form-select"
                                name="roleId"
                                value={formData.roleId}
                                onChange={handleChange}
                            >

                                <option value={1}>ADMIN</option>

                                <option value={2}>MANAGER</option>

                                <option value={3}>EMPLOYEE</option>

                            </select>

                        </div>

                        <div className="modal-footer">

                            {/* Modalı kapatır. */}
                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={onClose}
                            >

                                Cancel

                            </button>

                            {/* Kullanıcı ekler veya günceller. */}
                            <button
                                type="submit"
                                className="btn btn-primary"
                            >

                                {

                                    selectedUser

                                        ? "Update"

                                        : "Save"

                                }

                            </button>

                        </div>

                    </form>

                </div>

            </div>

        </div>

    );

}

export default UserModal;