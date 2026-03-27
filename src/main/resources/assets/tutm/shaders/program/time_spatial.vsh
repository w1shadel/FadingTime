#version 150

in vec4 Position;
out vec2 texCoord;

uniform mat4 ProjMat;
uniform vec2 InSize;

void main() {
    gl_Position = ProjMat * vec4(Position.xyz, 1.0);
    gl_Position.z = 0.0;
    // 投影後の座標 [-1, 1] を UV 座標 [0, 1] に変換 (NDC変換)
    texCoord = gl_Position.xy * 0.5 + 0.5;
}
