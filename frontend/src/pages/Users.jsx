import { useEffect, useState } from "react";

import userService from "../services/userService";

import UserTable from "../components/users/UserTable";
import UserModal from "../components/users/UserModal";
import Sidebar from "../components/Sidebar";
import LoadingSpinner from "../components/common/LoadingSpinner";

import "../styles/Users.css";

function Users() {

    // Kullanıcı listesini tutar.
    const [users, setUsers] = useState([]);

    // Sayfanın yüklenme durumunu tutar.
    const [loading, setLoading] = useState(true);

    // Oluşan hata mesajını tutar.
    const [error, setError] = useState("");

    // Modalın açık olup olmadığını tutar.
    const [showModal, setShowModal] = useState(false);

    // Düzenlenecek kullanıcıyı tutar.
    const [selectedUser, setSelectedUser] = useState(null);

    // Backend'den tüm kullanıcıları getirir.
    const loadUsers = async () => {

        try {

            const response = await userService.getAllUsers();

            setUsers(response);

        } catch (error) {

            console.error(error);

            if (error.response?.status === 403) {

                setError("You are not authorized to access this page.");

            } else {

                setError("Failed to load users.");

            }

        } finally {

            setLoading(false);

        }

    };

    // Düzenleme modalını açar.
    const handleEdit = (user) => {

        setSelectedUser(user);

        setShowModal(true);

    };

    // Kullanıcıyı siler.
    const handleDelete = async (id) => {

        try {

            await userService.deleteUser(id);

            await loadUsers();

        } catch (error) {

            // Beklenen 409 hatasını konsola yazdırmaz.
            if (error.response?.status !== 409) {

                console.error(error);

            }

            // Kullanıcının belgeleri varsa ikinci onay ister.
            if (error.response?.status === 409) {

                const confirmed = window.confirm(

                    error.response.data.message +

                    "\n\nAll owned documents will also be deleted.\n\nDo you want to continue?"

                );

                if (!confirmed) {

                    return;

                }

                // Belgelerle birlikte kullanıcıyı siler.
                await userService.deleteUser(id, true);

                await loadUsers();

                return;

            }

            if (error.response?.data?.message) {

                alert(error.response.data.message);

            } else {

                alert("Failed to delete user.");

            }

        }

    };

    useEffect(() => {

        const loadData = async () => {

            await loadUsers();

        };

        loadData().catch(console.error);

    }, []);

    if (loading) {

        return <LoadingSpinner />;

    }

    // Hata oluşursa gösterilir.
    if (error) {

        return <h2>{error}</h2>;

    }

    return (

        // Users sayfasının tamamı
        <div className="documents">

            {/* Sol Menü */}
            <Sidebar />

            {/* Sağ taraftaki içerik */}
            <main className="documents-content">

                {/* Sayfa başlığı */}
                <section className="documents-header">

                    <div>

                        <h1>Users</h1>

                        <p>

                            Manage system users and their roles.

                        </p>

                    </div>

                    {/* Yeni kullanıcı ekleme butonu */}
                    <button
                        className="upload-button"
                        onClick={() => {

                            // Yeni kullanıcı ekleme modunu açar.
                            setSelectedUser(null);

                            setShowModal(true);

                        }}
                    >

                        Add User

                    </button>

                </section>

                {/* Kullanıcı tablosu */}
                <UserTable

                    users={users}

                    onEdit={handleEdit}

                    onDelete={handleDelete}

                />

                {/* Kullanıcı ekleme / güncelleme modalı */}
                {

                    showModal && (

                        <UserModal

                            selectedUser={selectedUser}

                            onClose={() => {

                                setShowModal(false);

                                setSelectedUser(null);

                            }}

                            onSuccess={loadUsers}

                        />

                    )

                }

            </main>

        </div>

    );

}

export default Users;