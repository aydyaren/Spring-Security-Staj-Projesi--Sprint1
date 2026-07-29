function UserTable({ users, onEdit, onDelete }) {

    return (

        <table className="table table-bordered table-hover">

            <thead className="table-dark">

            <tr>

                <th>ID</th>

                <th>First Name</th>

                <th>Last Name</th>

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

                        <td>{user.firstName}</td>

                        <td>{user.lastName}</td>

                        <td>{user.username}</td>

                        <td>{user.email}</td>

                        <td>{user.role}</td>

                        <td>

                            <button
                                className="btn btn-warning btn-sm me-2"
                                onClick={() => onEdit(user)}
                            >

                                Edit

                            </button>

                            <button
                                className="btn btn-danger btn-sm"
                                onClick={() => onDelete(user.id)}
                            >

                                Delete

                            </button>

                        </td>

                    </tr>

                ))

            }

            </tbody>

        </table>

    );

}

export default UserTable;