#version 300 es
precision mediump float;

in vec2 v_texCoord;

layout (location = 0) out vec4 outColor;
vec3 color = vec3(1.);

void main()
{
    outColor = vec4(color, 0.5);
}