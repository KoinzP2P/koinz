#!/bin/bash
# KOINZ Docker Build Script
# Builds all KOINZ Docker images with proper versioning and metadata

set -e

# Configuration
VERSION=${VERSION:-"1.0.0"}
BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
VCS_REF=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
REGISTRY=${REGISTRY:-"koinz"}
PLATFORM=${PLATFORM:-"linux/amd64,linux/arm64"}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to build a Docker image
build_image() {
    local service=$1
    local dockerfile=$2
    local context=${3:-"."}
    
    log_info "Building $service image..."
    
    # Build arguments
    local build_args=(
        "--build-arg" "VERSION=$VERSION"
        "--build-arg" "BUILD_DATE=$BUILD_DATE"
        "--build-arg" "VCS_REF=$VCS_REF"
    )
    
    # Image tags
    local image_name="$REGISTRY/$service"
    local tags=(
        "--tag" "$image_name:$VERSION"
        "--tag" "$image_name:latest"
    )
    
    # Build the image
    if docker build \
        "${build_args[@]}" \
        "${tags[@]}" \
        --file "$dockerfile" \
        --platform "$PLATFORM" \
        "$context"; then
        log_success "Built $service image successfully"
        return 0
    else
        log_error "Failed to build $service image"
        return 1
    fi
}

# Function to test an image
test_image() {
    local service=$1
    local image_name="$REGISTRY/$service:$VERSION"
    
    log_info "Testing $service image..."
    
    # Basic image inspection
    if docker inspect "$image_name" >/dev/null 2>&1; then
        log_success "$service image exists and is valid"
        
        # Check image size
        local size=$(docker images --format "table {{.Size}}" "$image_name" | tail -n +2)
        log_info "$service image size: $size"
        
        return 0
    else
        log_error "$service image test failed"
        return 1
    fi
}

# Function to push images to registry
push_images() {
    if [ "$PUSH_IMAGES" = "true" ]; then
        log_info "Pushing images to registry..."
        
        for service in bitcoin tor seednode desktop api statsnode inventory; do
            local image_name="$REGISTRY/$service"
            log_info "Pushing $image_name..."
            
            if docker push "$image_name:$VERSION" && docker push "$image_name:latest"; then
                log_success "Pushed $service images"
            else
                log_error "Failed to push $service images"
            fi
        done
    fi
}

# Main build function
main() {
    log_info "Starting KOINZ Docker build process..."
    log_info "Version: $VERSION"
    log_info "Build Date: $BUILD_DATE"
    log_info "VCS Reference: $VCS_REF"
    log_info "Registry: $REGISTRY"
    log_info "Platform: $PLATFORM"
    echo
    
    # Check if Docker is available
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    # Check if we're in the correct directory
    if [ ! -f "gradlew" ]; then
        log_error "This script must be run from the KOINZ root directory"
        exit 1
    fi
    
    # Build Bitcoin Core image
    log_info "=== Building Bitcoin Core Image ==="
    if build_image "bitcoin" "docker/Dockerfile.bitcoin"; then
        test_image "bitcoin"
    else
        exit 1
    fi
    echo
    
    # Build Tor image
    log_info "=== Building Tor Image ==="
    if build_image "tor" "docker/Dockerfile.tor"; then
        test_image "tor"
    else
        exit 1
    fi
    echo
    
    # Build KOINZ Seednode image
    log_info "=== Building KOINZ Seednode Image ==="
    if build_image "seednode" "docker/Dockerfile.seednode"; then
        test_image "seednode"
    else
        exit 1
    fi
    echo
    
    # Build KOINZ Desktop image
    log_info "=== Building KOINZ Desktop Image ==="
    if build_image "desktop" "docker/Dockerfile.desktop"; then
        test_image "desktop"
    else
        log_warning "Desktop image build failed - continuing with other services"
    fi
    echo
    
    # Build KOINZ API image
    log_info "=== Building KOINZ API Image ==="
    if build_image "api" "docker/Dockerfile.api"; then
        test_image "api"
    else
        log_warning "API image build failed - continuing with other services"
    fi
    echo
    
    # Build statistics and inventory images (if Dockerfiles exist)
    if [ -f "docker/Dockerfile.statsnode" ]; then
        log_info "=== Building KOINZ Statistics Node Image ==="
        build_image "statsnode" "docker/Dockerfile.statsnode"
        test_image "statsnode"
        echo
    fi
    
    if [ -f "docker/Dockerfile.inventory" ]; then
        log_info "=== Building KOINZ Inventory Monitor Image ==="
        build_image "inventory" "docker/Dockerfile.inventory"
        test_image "inventory"
        echo
    fi
    
    # Push images if requested
    push_images
    
    log_success "KOINZ Docker build process completed!"
    
    # Display built images
    log_info "Built images:"
    docker images --filter "reference=$REGISTRY/*" --format "table {{.Repository}}:{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
}

# Script usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -v, --version VERSION    Set version tag (default: 1.0.0)"
    echo "  -r, --registry REGISTRY  Set registry prefix (default: koinz)"
    echo "  -p, --push              Push images to registry after build"
    echo "  --platform PLATFORM     Set target platform (default: linux/amd64,linux/arm64)"
    echo "  -h, --help              Show this help message"
    echo ""
    echo "Environment variables:"
    echo "  VERSION                 Version tag for images"
    echo "  REGISTRY                Registry prefix for images"
    echo "  PUSH_IMAGES             Set to 'true' to push images"
    echo "  PLATFORM                Target platform for builds"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -v|--version)
            VERSION="$2"
            shift 2
            ;;
        -r|--registry)
            REGISTRY="$2"
            shift 2
            ;;
        -p|--push)
            PUSH_IMAGES="true"
            shift
            ;;
        --platform)
            PLATFORM="$2"
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

# Execute main function
main "$@"