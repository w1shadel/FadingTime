#version 150

in vec4 Position;
out vec2 texCoord;

uniform mat4 ProjMat;
uniform vec2 InSize;

void main() {
    gl_Position = ProjMat * vec4(Position.xyz, 1.0);
    gl_Position.z = 0.0;
    texCoord = gl_Position.xy * 0.5 + 0.5;
}
