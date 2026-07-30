import { Pencil, Trash2 } from "lucide-react";

function UserTable({ users, onEdit, onDelete }) {

    const getRoleClass = (role) => {

        switch (role) {

            case "ADMIN":
                return "role-admin";

            case "MANAGER":
                return "role-manager";

            default:
                return "role-employee";

        }

    };

    return (

        <div className="documents-card">

            <table className="documents-table">

                <thead>

                <tr>

                    <th>ID</th>

                    <th>Name</th>

                    <th>Username</th>

                    <th>Email</th>

                    <th>Role</th>

                    <th>Actions</th>

                </tr>

                </thead>

                <tbody>

                {

                    users.map((user) => (

                        <tr key={user.id}>

                            <td>{user.id}</td>

                            <td>

                                {user.firstName} {user.lastName}

                            </td>

                            <td>{user.username}</td>

                            <td>{user.email}</td>

                            <td>

                                <span className={`role-badge ${getRoleClass(user.role)}`}>

                                    {user.role}

                                </span>

                            </td>

                            <td className="action-buttons">

                                <button
                                    className="table-icon-button edit-button"
                                    onClick={() => onEdit(user)}
                                    title="Edit User"
                                >

                                    <Pencil size={18} />

                                </button>

                                <button
                                    className="table-icon-button delete-button"
                                    onClick={() => onDelete(user.id)}
                                    title="Delete User"
                                >

                                    <Trash2 size={18} />

                                </button>

                            </td>

                        </tr>

                    ))

                }

                </tbody>

            </table>

        </div>

    );

}

export default UserTable;