#version 150

uniform sampler2D DiffuseSampler;
uniform float Intensity;// 0.0 to 1.0 based on proximity
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord - 0.5;
    float aspect = 16.0 / 9.0;
    vec2 correctedUV = uv * vec2(aspect, 1.0);
    float dist = length(correctedUV);
    float shift = Intensity * 0.2 * dist;
    vec2 shiftVec = uv * shift;

    float r = texture(DiffuseSampler, texCoord + shiftVec).r;
    float g = texture(DiffuseSampler, texCoord).g;
    float b = texture(DiffuseSampler, texCoord - shiftVec).b;
    vec3 color = vec3(r, g, b);
    float luma = dot(color, vec3(0.299, 0.587, 0.114));
    float saturationFactor = 1.0 + (0.4 * Intensity);
    color = mix(vec3(luma), color, saturationFactor);
    float vignette = smoothstep(0.8, 0.4, dist);
    float vignetteTarget = mix(0.7, 1.0, vignette);
    color *= mix(1.0, vignetteTarget, Intensity);

    fragColor = vec4(color, 1.0);
}
