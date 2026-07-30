import { useEffect, useState } from "react";

import userService from "../../services/userService";

function UserModal({

                       selectedUser,

                       onClose,

                       onSuccess

                   }) {

    const [error, setError] = useState("");

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

    useEffect(() => {

        // Modal açıkken arka planın scroll olmasını engeller.
        document.body.style.overflow = "hidden";

        const handleEsc = (event) => {

            if (event.key === "Escape") {

                onClose();

            }

        };

        window.document.addEventListener("keydown", handleEsc);

        return () => {

            // Modal kapanınca sayfanın scroll'unu tekrar açar.
            document.body.style.overflow = "auto";

            window.document.removeEventListener("keydown", handleEsc);

        };

    }, [onClose]);

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData({

            ...formData,

            [name]: name === "roleId" ? Number(value) : value

        });

    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        setError("");

        try {

            if (selectedUser) {

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

                await userService.createUser(formData);

            }

            await onSuccess();

            onClose();

        } catch (error) {

            if (error.response?.data?.message) {

                setError(error.response.data.message);

            } else {

                setError("Operation failed.");

            }

        }

    };

    return (

        <div className="modal-overlay">

            <div className="document-modal">

                <div className="modal-header">

                    <h2>

                        {

                            selectedUser

                                ? "Edit User"

                                : "Add User"

                        }

                    </h2>

                    <button

                        className="close-button"

                        onClick={onClose}

                    >

                        ✕

                    </button>

                </div>

                <form

                    className="modal-form"

                    onSubmit={handleSubmit}

                >

                    <div className="modal-body">

                        {

                            error && (

                                <div className="modal-error">

                                    {error}

                                </div>

                            )

                        }

                        <input

                            name="firstName"

                            placeholder="First Name"

                            value={formData.firstName}

                            onChange={handleChange}

                            required

                        />

                        <input

                            name="lastName"

                            placeholder="Last Name"

                            value={formData.lastName}

                            onChange={handleChange}

                            required

                        />

                        <input

                            name="username"

                            placeholder="Username"

                            value={formData.username}

                            onChange={handleChange}

                            required

                        />

                        <input

                            type="email"

                            name="email"

                            placeholder="Email"

                            value={formData.email}

                            onChange={handleChange}

                            required

                        />

                        {

                            !selectedUser && (

                                <input

                                    type="password"

                                    name="password"

                                    placeholder="Password"

                                    value={formData.password}

                                    onChange={handleChange}

                                    required

                                />

                            )

                        }

                        <select

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

                        <button

                            type="button"

                            className="cancel-btn"

                            onClick={onClose}

                        >

                            Cancel

                        </button>

                        <button

                            type="submit"

                            className="save-btn"

                        >

                            {

                                selectedUser

                                    ? "Update User"

                                    : "Create User"

                            }

                        </button>

                    </div>

                </form>

            </div>

        </div>

    );

}

export default UserModal;