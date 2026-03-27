#version 150

uniform sampler2D DiffuseSampler;
uniform float StopProgress;
uniform float Time;
uniform float AccelFactor;// 加速倍率 (1.0以上)
uniform float Mode;// 1.0:加速, 2.0:停止, 3.0:逆行

in vec2 texCoord;
out vec4 fragColor;

void main() {
    // --- 共通の座標計算 ---
    vec2 uv = texCoord - 0.5;
    float aspect = 16.0 / 9.0;
    vec2 correctedUV = uv * vec2(aspect, 1.0);
    float dist = length(correctedUV);

    vec4 finalOutput;

    if (Mode == 1.0) {
        // === 加速エフェクト (TIME ACCELERATION) ===
        float shift = (AccelFactor - 1.0) * 0.015 * dist;
        vec2 shiftVec = uv * shift;

        float r = texture(DiffuseSampler, texCoord + shiftVec).r;
        float g = texture(DiffuseSampler, texCoord).g;
        float b = texture(DiffuseSampler, texCoord - shiftVec).b;
        vec3 color = vec3(r, g, b);

        float luma = dot(color, vec3(0.299, 0.587, 0.114));
        color = mix(vec3(luma), color, 1.4);

        float vignette = smoothstep(0.8, 0.4, dist);
        color *= mix(0.7, 1.0, vignette);

        finalOutput = vec4(color, 1.0);

    } else if (Mode == 2.0) {
        // === 停止エフェクト (TIME STOP) ===
        float radius = StopProgress * 1.5;
        float expansionEdgeMask = smoothstep(0.15, 0.0, abs(dist - radius));
        float finishProgress = smoothstep(0.8, 1.0, StopProgress);
        float screenEdgeMask = smoothstep(0.4, 0.8, dist) * finishProgress;

        float wave = sin(Time * 2.0 + (texCoord.y + texCoord.x) * 15.0);
        float wave2 = cos(Time * 1.5 + (texCoord.y - texCoord.x) * 20.0);
        vec2 distortionDir = vec2(wave, wave2) * 0.01;
        vec2 finalDistortion = distortionDir * (expansionEdgeMask * 2.0 + screenEdgeMask * 1.5);

        vec2 distortedCoord = texCoord + finalDistortion;
        vec4 col = texture(DiffuseSampler, distortedCoord);

        float luma = dot(col.rgb, vec3(0.299, 0.587, 0.114));
        vec3 grayCol = vec3(luma);
        float fillFactor = smoothstep(radius - 0.1, radius, dist);
        vec3 finalColor = mix(grayCol, col.rgb, fillFactor);

        float border = expansionEdgeMask * smoothstep(0.0, 1.0, 1.0 - StopProgress);
        finalColor += vec3(0.7, 0.85, 1.0) * border * 1.5;
        finalColor *= 1.0 - (screenEdgeMask * 0.2);

        finalOutput = vec4(finalColor, 1.0);

    } else if (Mode == 3.0) {
        // === 遡行エフェクト (TIME REWIND) ===
        // 1. 強めの色収差 (RGBを横に大きくずらす)
        float shift = 0.01 + sin(Time * 20.0) * 0.005;
        float r = texture(DiffuseSampler, texCoord + vec2(shift, 0.0)).r;
        float g = texture(DiffuseSampler, texCoord).g;
        float b = texture(DiffuseSampler, texCoord - vec2(shift, 0.0)).b;
        vec3 color = vec3(r, g, b);

        // 2. 青みがかったモノクロ（冷たい過去の表現）
        float luma = dot(color, vec3(0.299, 0.587, 0.114));
        vec3 blueTint = vec3(luma * 0.4, luma * 0.8, luma * 1.5);
        color = mix(color, blueTint, 0.7);

        // 3. VCR風スキャンライン (横線)
        float scanline = sin(texCoord.y * 500.0 + Time * 15.0) * 0.05;
        color -= scanline;

        // 4. 画面全体のフリッカー (チカチカ)
        float flicker = sin(Time * 100.0) * 0.02;
        color += flicker;

        // 5. 周辺減光 (ビネット)
        float vignette = smoothstep(1.0, 0.5, dist);
        color *= vignette;

        finalOutput = vec4(color, 1.0);

    } else {
        // 通常時
        finalOutput = texture(DiffuseSampler, texCoord);
    }

    fragColor = finalOutput;
}