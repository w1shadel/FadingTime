#version 150
uniform sampler2D DiffuseSampler;
uniform float StopProgress;
in vec2 texCoord;
out vec4 fragColor;

// Javaから送られてくる Uniform


void main(){
    vec4 col = texture(DiffuseSampler, texCoord);

    // 灰色化の計算
    float luma = dot(col.rgb, vec3(0.299, 0.587, 0.114));
    vec3 grayCol = vec3(luma);

    // StopProgress に応じて混ぜる
    vec3 outColor = mix(col.rgb, grayCol, StopProgress);

    fragColor = vec4(outColor, 1.0);
}