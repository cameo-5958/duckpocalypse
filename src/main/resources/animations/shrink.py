from PIL import Image

# === CONFIG ===
INPUT_PATH  = "src/main/resources/animations/tower-idle-base.png"          # your original image
OUTPUT_PATH = "src/main/resources/animations/tower-idle.png"   # output file
SPRITE_COUNT = 2

SRC_SIZE = 64
DEST_SIZE = 32

# how much to push sprites upward after bottom-centering
UP_OFFSET = 10   # change to 8–12 if needed


def main():
    sheet = Image.open(INPUT_PATH).convert("RGBA")

    out_width  = SPRITE_COUNT * SRC_SIZE
    out_height = SRC_SIZE

    output = Image.new("RGBA", (out_width, out_height), (0, 0, 0, 0))

    for i in range(SPRITE_COUNT):
        # 1. Crop original sprite
        x0 = i * SRC_SIZE
        y0 = 0
        sprite = sheet.crop((x0, y0, x0 + SRC_SIZE, y0 + SRC_SIZE))

        # 2. Resize to 32x32
        small = sprite.resize((DEST_SIZE, DEST_SIZE), Image.NEAREST)

        # 3. Compute bottom-centered placement
        dest_x = i * SRC_SIZE + (SRC_SIZE - DEST_SIZE) // 2
        dest_y = SRC_SIZE - DEST_SIZE - UP_OFFSET

        # 4. Paste into output canvas
        output.paste(small, (dest_x, dest_y), small)

    output.save(OUTPUT_PATH)
    print("Saved:", OUTPUT_PATH)


if __name__ == "__main__":
    main()
