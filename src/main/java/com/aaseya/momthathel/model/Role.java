package com.aaseya.momthathel.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps to the {@code ROLES} table.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    @SequenceGenerator(name = "roles_seq", sequenceName = "roles_role_id_seq", allocationSize = 1)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 100)
    private String roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    public Role() {}

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}
