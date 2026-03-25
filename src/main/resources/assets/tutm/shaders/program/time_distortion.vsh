#version 150

in vec4 Position;
out vec2 texCoord;

uniform mat4 ProjMat;

void main() {
    gl_Position = ProjMat * vec4(Position.xyz, 1.0);
    gl_Position.z = 0.0;
    // NDC [-1, 1] を UV [0, 1] に変換
    texCoord = gl_Position.xy * 0.5 + 0.5;
}
