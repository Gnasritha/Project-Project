class UserModel {
  final String id;
  final String name;
  final String role;
  final String? avatarUrl;

  const UserModel({
    required this.id,
    required this.name,
    required this.role,
    this.avatarUrl,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) => UserModel(
        id: json['id'] as String,
        name: json['name'] as String,
        role: json['role'] as String,
        avatarUrl: json['avatarUrl'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'role': role,
        'avatarUrl': avatarUrl,
      };
}
